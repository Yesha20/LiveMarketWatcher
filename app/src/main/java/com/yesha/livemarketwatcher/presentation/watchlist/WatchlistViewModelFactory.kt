package com.yesha.livemarketwatcher.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yesha.livemarketwatcher.domain.repository.MarketRepository
import com.yesha.livemarketwatcher.domain.usecase.DeterminePriceDirectionUseCase

class WatchlistViewModelFactory(
    private val repository: MarketRepository,
    private val determinePriceDirection: DeterminePriceDirectionUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {
            return WatchlistViewModel(
                repository = repository,
                determinePriceDirection = determinePriceDirection
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}