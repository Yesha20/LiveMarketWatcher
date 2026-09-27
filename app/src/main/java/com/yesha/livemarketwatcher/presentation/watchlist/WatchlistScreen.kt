package com.yesha.livemarketwatcher.presentation.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yesha.livemarketwatcher.domain.model.ConnectionState

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel
) {
    val prices by viewModel.prices.collectAsStateWithLifecycle()

    val connectionState by viewModel.connectionState
        .collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Live Market Watchlist",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = when (connectionState) {
                ConnectionState.CONNECTING -> "🟡 Connecting..."
                ConnectionState.CONNECTED -> "🟢 Live"
                ConnectionState.DISCONNECTED -> "⚪ Disconnected"
                ConnectionState.ERROR -> "🔴 Connection Error"
            },
            modifier = Modifier.padding(top = 4.dp)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 24.dp,
                    bottom = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Asset",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Price",
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = prices,
                key = { it.symbol }
            ) { price ->

                PriceRow(
                    price = price
                )

                HorizontalDivider()
            }
        }
    }
}