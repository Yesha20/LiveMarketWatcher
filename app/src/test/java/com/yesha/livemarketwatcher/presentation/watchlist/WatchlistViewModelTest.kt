package com.yesha.livemarketwatcher.presentation.watchlist

import com.yesha.livemarketwatcher.domain.model.ConnectionState
import com.yesha.livemarketwatcher.domain.model.PriceDirection
import com.yesha.livemarketwatcher.domain.model.PriceUpdate
import com.yesha.livemarketwatcher.domain.repository.MarketRepository
import com.yesha.livemarketwatcher.domain.usecase.DeterminePriceDirectionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WatchlistViewModelTest {

    private lateinit var viewModel: WatchlistViewModel
    private lateinit var priceUpdates: MutableSharedFlow<PriceUpdate>

    @Before
    fun setup() {
        priceUpdates = MutableSharedFlow(
            replay = 1
        )

        val fakeRepository = object : MarketRepository {

            override fun observePrices(): Flow<PriceUpdate> {
                return priceUpdates
            }

            override fun observeConnectionState(): Flow<ConnectionState> {
                return emptyFlow()
            }
        }

        viewModel = WatchlistViewModel(
            repository = fakeRepository,
            determinePriceDirection = DeterminePriceDirectionUseCase(),
            sharingStarted = SharingStarted.Eagerly
        )
    }

    @Test
    fun `first price update has NONE direction`() = runTest {

        val collectJob = launch {
            viewModel.prices.collect { }
        }

        priceUpdates.emit(
            PriceUpdate(
                symbol = "BTCUSDT",
                price = 100.0
            )
        )
        advanceUntilIdle()
        val result = viewModel.prices.first {
            it.size == 1
        }

        assertEquals("BTCUSDT", result[0].symbol)
        assertEquals(100.0, result[0].price, 0.0)
        assertEquals(
            PriceDirection.NONE,
            result[0].direction
        )

        collectJob.cancel()
    }

    @Test
    fun `higher price produces UP direction`() = runTest {

        val collectJob = launch {
            viewModel.prices.collect { }
        }

        priceUpdates.emit(
            PriceUpdate("BTCUSDT", 100.0)
        )

        viewModel.prices.first {
            it.size == 1
        }

        priceUpdates.emit(
            PriceUpdate("BTCUSDT", 101.0)
        )
        advanceUntilIdle()
        val result = viewModel.prices.first {
            it.firstOrNull()?.price == 101.0
        }

        assertEquals(
            PriceDirection.UP,
            result[0].direction
        )

        collectJob.cancel()
    }

    @Test
    fun `lower price produces DOWN direction`() = runTest {

        val collectJob = launch {
            viewModel.prices.collect { }
        }

        priceUpdates.emit(
            PriceUpdate("BTCUSDT", 100.0)
        )

        viewModel.prices.first {
            it.size == 1
        }

        priceUpdates.emit(
            PriceUpdate("BTCUSDT", 99.0)
        )
        advanceUntilIdle()
        val result = viewModel.prices.first {
            it.firstOrNull()?.price == 99.0
        }

        assertEquals(
            PriceDirection.DOWN,
            result[0].direction
        )

        collectJob.cancel()
    }

   }