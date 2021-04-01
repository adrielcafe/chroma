package cafe.adriel.chroma.model.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cafe.adriel.chroma.R
import cafe.adriel.chroma.view.components.SelectOption

enum class AccidentalOption(
    @StringRes override val labelRes: Int,
    @DrawableRes val symbolRes: Int
) : SelectOption<AccidentalOption> {
    SHARP(R.string.accidental_sharp, R.drawable.ic_sharp),
    FLAT(R.string.accidental_flat, R.drawable.ic_flat);

    companion object {
        const val titleRes = R.string.accidental
    }
}
