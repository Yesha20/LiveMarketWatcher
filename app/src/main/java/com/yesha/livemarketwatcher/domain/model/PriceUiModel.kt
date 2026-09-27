package com.yesha.livemarketwatcher.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class PriceUiModel(
    val symbol: String,
    val price: Double,
    val direction: PriceDirection = PriceDirection.NONE
)
