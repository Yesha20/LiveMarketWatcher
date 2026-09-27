package com.yesha.livemarketwatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yesha.livemarketwatcher.data.remote.MarketWebSocketDataSource
import com.yesha.livemarketwatcher.data.repository.MarketRepositoryImpl
import com.yesha.livemarketwatcher.domain.usecase.DeterminePriceDirectionUseCase
import com.yesha.livemarketwatcher.presentation.watchlist.WatchlistScreen
import com.yesha.livemarketwatcher.presentation.watchlist.WatchlistViewModel
import com.yesha.livemarketwatcher.presentation.watchlist.WatchlistViewModelFactory
import com.yesha.livemarketwatcher.ui.theme.LiveMarketWatcherTheme
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = OkHttpClient()

        val dataSource = MarketWebSocketDataSource(client)

        val repository = MarketRepositoryImpl(dataSource)

        val factory = WatchlistViewModelFactory(
            repository = repository,
            determinePriceDirection = DeterminePriceDirectionUseCase()
        )
        setContent {
            LiveMarketWatcherTheme {
                val viewModel: WatchlistViewModel = viewModel(
                    factory = factory
                )

                WatchlistScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}


