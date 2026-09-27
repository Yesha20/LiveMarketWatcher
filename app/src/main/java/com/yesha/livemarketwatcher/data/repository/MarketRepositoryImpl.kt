package com.yesha.livemarketwatcher.data.repository

import com.yesha.livemarketwatcher.data.remote.MarketWebSocketDataSource
import com.yesha.livemarketwatcher.domain.model.ConnectionState
import com.yesha.livemarketwatcher.domain.model.PriceUpdate
import com.yesha.livemarketwatcher.domain.repository.MarketRepository
import kotlinx.coroutines.flow.Flow

class MarketRepositoryImpl(
    private val dataSource: MarketWebSocketDataSource
) : MarketRepository {

    override fun observePrices(): Flow<PriceUpdate> {
        return dataSource.observePrices()
    }

    override fun observeConnectionState(): Flow<ConnectionState> {
        return dataSource.connectionState
    }
}