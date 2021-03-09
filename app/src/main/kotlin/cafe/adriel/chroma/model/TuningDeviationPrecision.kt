package cafe.adriel.chroma.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.chroma.view.compose.theme.ChromaColors

enum class TuningDeviationPrecision(
    val color: Color,
    val barHeight: Dp,
    val isActive: (deviation: Int, deviationOffset: Int) -> Boolean
) {
    INACCURATE_NEGATIVE_HIGH(
        color = ChromaColors.red,
        barHeight = 30.dp,
        isActive = { deviation, _ -> deviation <= -50 }
    ),
    INACCURATE_NEGATIVE_MEDIUM(
        color = ChromaColors.red,
        barHeight = 30.dp,
        isActive = { deviation, _ -> deviation in -40 downTo -49 }
    ),
    INACCURATE_NEGATIVE_LOW(
        color = ChromaColors.red,
        barHeight = 30.dp,
        isActive = { deviation, _ -> deviation in -30 downTo -39 }
    ),
    ACCURATE_NEGATIVE_MEDIUM(
        color = ChromaColors.yellow,
        barHeight = 60.dp,
        isActive = { deviation, _ -> deviation in -20 downTo -29 }
    ),
    ACCURATE_NEGATIVE_LOW(
        color = ChromaColors.yellow,
        barHeight = 60.dp,
        isActive = { deviation, precisionOffset -> deviation in -precisionOffset - 1 downTo -19 }
    ),
    VERY_ACCURATE(
        color = ChromaColors.green,
        barHeight = 100.dp,
        isActive = { deviation, precisionOffset -> deviation in -precisionOffset..precisionOffset }
    ),
    ACCURATE_POSITIVE_LOW(
        color = ChromaColors.yellow,
        barHeight = 60.dp,
        isActive = { deviation, precisionOffset -> deviation in precisionOffset + 1 until 20 }
    ),
    ACCURATE_POSITIVE_MEDIUM(
        color = ChromaColors.yellow,
        barHeight = 60.dp,
        isActive = { deviation, _ -> deviation in 20 until 30 }
    ),
    INACCURATE_POSITIVE_LOW(
        color = ChromaColors.red,
        barHeight = 30.dp,
        isActive = { deviation, _ -> deviation in 30 until 40 }
    ),
    INACCURATE_POSITIVE_MEDIUM(
        color = ChromaColors.red,
        barHeight = 30.dp,
        isActive = { deviation, _ -> deviation in 40 until 50 }
    ),
    INACCURATE_POSITIVE_HIGH(
        color = ChromaColors.red,
        barHeight = 30.dp,
        isActive = { deviation, _ -> deviation >= 50 }
    );

    companion object {

        fun fromDeviation(deviation: Int, offset: Int): TuningDeviationPrecision =
            values().first { item ->
                item.isActive(deviation, offset)
            }
    }
}
