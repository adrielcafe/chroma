package cafe.adriel.chroma.view.compose.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import cafe.adriel.chroma.R

object ChromaTypography {

    private val notoSansFontFamily = FontFamily(Font(R.font.noto_sans))

    operator fun invoke() =
        Typography(
            defaultFontFamily = notoSansFontFamily
        )
}
