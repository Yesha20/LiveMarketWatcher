package com.yesha.livemarketwatcher.presentation.watchlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yesha.livemarketwatcher.domain.model.PriceDirection
import com.yesha.livemarketwatcher.domain.model.PriceUiModel
import java.util.Locale

@Composable
fun PriceRow(
    price: PriceUiModel
) {
    val flashColor by animateColorAsState(
        targetValue = when (price.direction) {
            PriceDirection.UP ->
                Color.Green.copy(alpha = 0.30f)

            PriceDirection.DOWN ->
                Color.Red.copy(alpha = 0.30f)

            PriceDirection.NONE ->
                Color.Transparent
        },
        animationSpec = tween(800),
        label = "priceFlash"
    )

    val displayName = when (price.symbol.uppercase()) {
        "BTCUSDT" -> "Bitcoin"
        "ETHUSDT" -> "Ethereum"
        "BNBUSDT" -> "BNB"
        "SOLUSDT" -> "Solana"
        "XRPUSDT" -> "XRP"
        else -> price.symbol
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(
                text = displayName,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = price.symbol,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Text(
            text = String.format(
                Locale.US,
                "%.2f",
                price.price
            ),
            modifier = Modifier
                .background(
                    color = flashColor,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
        )
    }
}