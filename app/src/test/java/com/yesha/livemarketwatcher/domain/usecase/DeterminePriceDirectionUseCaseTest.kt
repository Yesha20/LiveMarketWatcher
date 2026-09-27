package com.yesha.livemarketwatcher.domain.usecase

import com.yesha.livemarketwatcher.domain.model.PriceDirection
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeterminePriceDirectionUseCaseTest {

    private lateinit var useCase: DeterminePriceDirectionUseCase

    @Before
    fun setup() {
        useCase = DeterminePriceDirectionUseCase()
    }

    @Test
    fun `returns NONE when there is no previous price`() {
        val result = useCase(
            previousPrice = null,
            currentPrice = 100.0
        )

        assertEquals(PriceDirection.NONE, result)
    }

    @Test
    fun `returns UP when current price is higher`() {
        val result = useCase(
            previousPrice = 100.0,
            currentPrice = 101.0
        )

        assertEquals(PriceDirection.UP, result)
    }

    @Test
    fun `returns DOWN when current price is lower`() {
        val result = useCase(
            previousPrice = 100.0,
            currentPrice = 99.0
        )

        assertEquals(PriceDirection.DOWN, result)
    }

    @Test
    fun `returns NONE when price does not change`() {
        val result = useCase(
            previousPrice = 100.0,
            currentPrice = 100.0
        )

        assertEquals(PriceDirection.NONE, result)
    }
}