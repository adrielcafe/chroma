package cafe.adriel.chroma.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DonationProduct(val sku: String, val label: String, val alignment: Alignment, val offset: Dp) {
    COFFEE_1("coffee_1", "x1", Alignment.CenterStart, Dp.Hairline),
    COFFEE_3("coffee_3", "x3", Alignment.Center, 91.dp),
    COFFEE_5("coffee_5", "x5", Alignment.CenterEnd, 182.dp)
}
