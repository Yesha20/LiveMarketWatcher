package com.yesha.livemarketwatcher.data.remote

import com.yesha.livemarketwatcher.domain.model.ConnectionState
import com.yesha.livemarketwatcher.domain.model.PriceUpdate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class MarketWebSocketDataSource(
    private val client: OkHttpClient
) {

    private val _connectionState =
        MutableStateFlow(ConnectionState.DISCONNECTED)

    val connectionState: StateFlow<ConnectionState> =
        _connectionState

    private val symbols = listOf(
        "btcusdt",
        "ethusdt",
        "bnbusdt",
        "solusdt",
        "xrpusdt"
    )

    fun observePrices(): Flow<PriceUpdate> = callbackFlow {

        val streams = symbols.joinToString("/") {
            "$it@ticker"
        }

        val url =
            "wss://stream.binance.com:9443/stream?streams=$streams"

        val request = Request.Builder()
            .url(url)
            .build()

        var webSocket: WebSocket? = null
        var reconnectAttempt = 0

        fun connect() {

            _connectionState.value = ConnectionState.CONNECTING

            webSocket = client.newWebSocket(
                request,
                object : WebSocketListener() {

                    override fun onOpen(
                        webSocket: WebSocket,
                        response: Response
                    ) {
                        reconnectAttempt = 0

                        _connectionState.value =
                            ConnectionState.CONNECTED
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        text: String
                    ) {
                        try {
                            val root = JSONObject(text)
                            val data = root.getJSONObject("data")

                            val symbol = data.getString("s")
                            val price = data.getDouble("c")

                            trySend(
                                PriceUpdate(
                                    symbol = symbol,
                                    price = price
                                )
                            )
                        } catch (e: Exception) {
                            // Ignore malformed messages
                        }
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        _connectionState.value =
                            ConnectionState.ERROR

                        this@callbackFlow.launch {

                            reconnectAttempt++

                            val delayMillis = when (reconnectAttempt) {
                                1 -> 2_000L
                                2 -> 4_000L
                                3 -> 8_000L
                                else -> 15_000L
                            }

                            delay(delayMillis)

                            connect()
                        }
                    }

                    override fun onClosed(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String
                    ) {
                        _connectionState.value =
                            ConnectionState.DISCONNECTED
                    }
                }
            )
        }

        connect()

        awaitClose {
            webSocket?.close(
                1000,
                "Flow collection cancelled"
            )

            _connectionState.value =
                ConnectionState.DISCONNECTED
        }
    }
}