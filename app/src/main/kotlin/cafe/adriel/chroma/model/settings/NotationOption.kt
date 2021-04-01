package cafe.adriel.chroma.model.settings

import androidx.annotation.StringRes
import cafe.adriel.chroma.R
import cafe.adriel.chroma.view.components.SelectOption

enum class NotationOption(
    @StringRes override val labelRes: Int
) : SelectOption<NotationOption> {
    A_B_C(R.string.notation_a_b_c),
    DO_RE_MI(R.string.notation_do_re_mi);

    companion object {
        const val titleRes = R.string.notation
    }
}
