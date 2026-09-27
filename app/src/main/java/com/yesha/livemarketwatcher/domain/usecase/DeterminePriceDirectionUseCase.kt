package com.yesha.livemarketwatcher.domain.usecase

import com.yesha.livemarketwatcher.domain.model.PriceDirection

class DeterminePriceDirectionUseCase {

    operator fun invoke(
        previousPrice: Double?,
        currentPrice: Double
    ): PriceDirection {
        return when {
            previousPrice == null -> PriceDirection.NONE
            currentPrice > previousPrice -> PriceDirection.UP
            currentPrice < previousPrice -> PriceDirection.DOWN
            else -> PriceDirection.NONE
        }
    }
}