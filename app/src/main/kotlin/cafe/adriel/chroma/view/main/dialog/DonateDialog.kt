package cafe.adriel.nomanswallpaper.view.main.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.color
import cafe.adriel.chroma.util.px
import cafe.adriel.chroma.view.custom.DonateView

class DonateDialog private constructor() {

    companion object {
        fun show(context: Context) {
            val donateView = DonateView(context)
            AlertDialog.Builder(context)
                .setTitle(R.string.buy_me_coffee)
                .setView(donateView)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.donate) { _, _ ->
                    val listener = context as OnDonateListener?
                    listener?.onDonate(donateView.selectedProductSku)
                }
                .create()
                .run {
                    setOnShowListener {
                        val coffeeIcon = context.getDrawable(R.drawable.ic_free_breakfast)?.apply {
                            DrawableCompat.setTint(this, context.color(R.color.colorAccent))
                        }
                        with(getButton(AlertDialog.BUTTON_POSITIVE)) {
                            setCompoundDrawablesRelativeWithIntrinsicBounds(
                                coffeeIcon,
                                null,
                                null,
                                null
                            )
                            compoundDrawablePadding = 5.px
                        }
                    }
                    show()
                }
        }
    }

    interface OnDonateListener {
        fun onDonate(sku: String)
    }

}