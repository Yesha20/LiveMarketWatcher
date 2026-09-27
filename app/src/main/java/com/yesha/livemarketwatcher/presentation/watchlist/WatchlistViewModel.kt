package com.yesha.livemarketwatcher.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yesha.livemarketwatcher.domain.model.PriceDirection
import com.yesha.livemarketwatcher.domain.model.PriceUiModel
import com.yesha.livemarketwatcher.domain.repository.MarketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import com.yesha.livemarketwatcher.domain.model.ConnectionState
import com.yesha.livemarketwatcher.domain.usecase.DeterminePriceDirectionUseCase

class WatchlistViewModel(
    private val repository: MarketRepository,
    private val determinePriceDirection: DeterminePriceDirectionUseCase,
    private val sharingStarted: SharingStarted =
        SharingStarted.WhileSubscribed(5_000)
) : ViewModel() {

    val connectionState = repository
        .observeConnectionState()
        .stateIn(
            scope = viewModelScope,
            started = sharingStarted,
            initialValue = ConnectionState.DISCONNECTED
        )

    val prices = repository
        .observePrices()
        .scan(emptyList<PriceUiModel>()) { currentList, update ->

            val previousPrice = currentList
                .find { it.symbol == update.symbol }
                ?.price

            val direction = determinePriceDirection(
                previousPrice = previousPrice,
                currentPrice = update.price
            )

            val updatedPrice = PriceUiModel(
                symbol = update.symbol,
                price = update.price,
                direction = direction
            )

            currentList
                .filter { it.symbol != update.symbol } + updatedPrice
        }
        .stateIn(
            scope = viewModelScope,
            started = sharingStarted,
            initialValue = emptyList()
        )
}