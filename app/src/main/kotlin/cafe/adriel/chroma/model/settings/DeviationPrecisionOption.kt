package cafe.adriel.chroma.model.settings

import androidx.annotation.StringRes
import cafe.adriel.chroma.R
import cafe.adriel.chroma.view.components.SelectOption

enum class DeviationPrecisionOption(
    @StringRes override val labelRes: Int,
    val offset: Int
) : SelectOption<DeviationPrecisionOption> {
    Zero(R.string.deviation_precision_0, 0),
    One(R.string.deviation_precision_1, 1),
    Two(R.string.deviation_precision_2, 2),
    Three(R.string.deviation_precision_3, 3),
    Four(R.string.deviation_precision_4, 4),
    Five(R.string.deviation_precision_5, 5);

    companion object {
        const val titleRes = R.string.precision
    }
}
