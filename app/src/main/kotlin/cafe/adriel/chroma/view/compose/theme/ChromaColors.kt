package cafe.adriel.chroma.view.compose.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object ChromaColors {
    private val primary = Color(0xFF34495E)
    private val primaryVariant = Color(0xFF2C3E50)
    private val secondary = Color(0xFF1ABC9C)
    private val secondaryVariant = Color(0xFF14967C)

    val red = Color(0xFFE74C3C)
    val yellow = Color(0xFFF1C40F)
    val green = Color(0xFF2ECC71)
    val gray = Color(0xFFBDBDBD)

    operator fun invoke() =
        darkColors(
            primary = primary,
            primaryVariant = primaryVariant,
            secondary = secondary,
            secondaryVariant = secondaryVariant,
            background = primaryVariant,
            surface = primaryVariant
        )
}
