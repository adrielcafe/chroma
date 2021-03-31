package cafe.adriel.chroma.model.settings.options

import androidx.annotation.StringRes
import cafe.adriel.chroma.R
import cafe.adriel.chroma.view.components.SelectOption

enum class AccidentalOption(
    @StringRes override val labelRes: Int,
    @StringRes val symbolRes: Int
) : SelectOption<AccidentalOption> {
    SHARP(R.string.accidental_sharp, R.string.accidental_sharp_symbol),
    FLAT(R.string.accidental_flat, R.string.accidental_flat_symbol);

    companion object {
        const val titleRes = R.string.accidental
    }
}
