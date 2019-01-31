package cafe.adriel.chroma.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.getSystemService
import cafe.adriel.chroma.App
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.color
import kotlinx.android.synthetic.main.view_donate.view.*

class DonateView(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    var selectedProductSku = App.PRODUCT_SKU_COFFEE_1
        private set

    init {
        context.getSystemService<LayoutInflater>()?.inflate(R.layout.view_donate, this, true)?.run {
            updateTextColor(v1Coffee, true)
            vSlider.setOnDiscreteSliderChangeListener(::onOptionSelected)
            v1Coffee.setOnClickListener {
                vSlider.position = 0
                onOptionSelected(vSlider.position)
            }
            v3Coffee.setOnClickListener {
                vSlider.position = 1
                onOptionSelected(vSlider.position)
            }
            v5Coffee.setOnClickListener {
                vSlider.position = 2
                onOptionSelected(vSlider.position)
            }

        }
    }

    private fun onOptionSelected(position: Int){
        updateTextColor(v1Coffee, false)
        updateTextColor(v3Coffee, false)
        updateTextColor(v5Coffee, false)
        when (position) {
            0 -> {
                selectedProductSku = App.PRODUCT_SKU_COFFEE_1
                updateTextColor(v1Coffee, true)
            }
            1 -> {
                selectedProductSku = App.PRODUCT_SKU_COFFEE_3
                updateTextColor(v3Coffee, true)
            }
            2 -> {
                selectedProductSku = App.PRODUCT_SKU_COFFEE_5
                updateTextColor(v5Coffee, true)
            }
        }
    }

    private fun updateTextColor(view: AppCompatTextView, selected: Boolean) {
        view.setTextColor(
            color(if (selected) R.color.colorAccent else R.color.grey_400)
        )
    }

}