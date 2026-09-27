package com.yesha.livemarketwatcher.domain.repository

import com.yesha.livemarketwatcher.domain.model.ConnectionState
import com.yesha.livemarketwatcher.domain.model.PriceUpdate
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    fun observePrices(): Flow<PriceUpdate>

    fun observeConnectionState(): Flow<ConnectionState>
}