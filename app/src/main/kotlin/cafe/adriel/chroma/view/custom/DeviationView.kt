package cafe.adriel.chroma.view.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.core.content.getSystemService
import androidx.core.util.forEach
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.color
import cafe.adriel.chroma.util.getDeviationColorRes
import kotlin.properties.Delegates
import kotlinx.android.synthetic.main.view_deviation.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeviationView(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    companion object {
        private const val ALPHA_DISABLED = .2F
        private const val ALPHA_ENABLED = 1F

        private const val ANIMATION_DURATION_SHORT = 100L
        private const val ANIMATION_DURATION_LONG = 500L
    }

    private val deviationBars = SparseIntArray().apply {
        put(-50, R.id.vDeviationMinus50)
        put(-40, R.id.vDeviationMinus40)
        put(-30, R.id.vDeviationMinus30)
        put(-20, R.id.vDeviationMinus20)
        put(-10, R.id.vDeviationMinus10)
        put(0, R.id.vDeviationZero)
        put(10, R.id.vDeviationPlus10)
        put(20, R.id.vDeviationPlus20)
        put(30, R.id.vDeviationPlus30)
        put(40, R.id.vDeviationPlus40)
        put(50, R.id.vDeviationPlus50)
    }

    var precision: Int = 0
    var deviation: Int by Delegates.observable(0) { _, _, new ->
        onDeviationChange(new)
    }

    init {
        context.getSystemService<LayoutInflater>()?.inflate(R.layout.view_deviation, this, true)
    }

    private fun onDeviationChange(deviation: Int) {
        val deviationColor = color(getDeviationColorRes(deviation, precision))
        resetBars()
        when {
            deviation in -precision..precision -> {
                vDeviationZero.setBackgroundColor(deviationColor)
                vDeviationZero.alpha = ALPHA_ENABLED
            }
            deviation <= -50 -> {
                vDeviationMinus50.setBackgroundColor(deviationColor)
                vDeviationMinus50.alpha = ALPHA_ENABLED
            }
            deviation >= 50 -> {
                vDeviationPlus50.setBackgroundColor(deviationColor)
                vDeviationPlus50.alpha = ALPHA_ENABLED
            }
            deviation < -precision -> deviationBars.forEach { dev, resId ->
                if (dev < 0 && deviation in dev..dev + 10) {
                    findViewById<View>(resId).let {
                        it.setBackgroundColor(deviationColor)
                        it.alpha = ALPHA_ENABLED
                        return
                    }
                }
            }
            deviation > precision -> deviationBars.forEach { dev, resId ->
                if (dev > 0 && deviation in dev - 10..dev) {
                    findViewById<View>(resId).let {
                        it.setBackgroundColor(deviationColor)
                        it.alpha = ALPHA_ENABLED
                        return
                    }
                }
            }
        }
    }

    private fun resetBars() {
        deviationBars.forEach { _, resId ->
            findViewById<View>(resId).let {
                it.setBackgroundColor(Color.WHITE)
                it.alpha = ALPHA_DISABLED
            }
        }
    }

    fun animateBars() {
        suspend fun animate(@ColorRes colorRes: Int, duration: Long, vararg views: View) {
            resetBars()
            views.forEach {
                it.setBackgroundColor(color(colorRes))
                it.alpha = ALPHA_ENABLED
            }
            delay(duration)
        }

        (context as? LifecycleOwner)
            ?.lifecycleScope
            ?.launch {
                delay(ANIMATION_DURATION_LONG)

                animate(R.color.red, ANIMATION_DURATION_SHORT, vDeviationMinus50, vDeviationPlus50)
                animate(R.color.red, ANIMATION_DURATION_SHORT, vDeviationMinus40, vDeviationPlus40)
                animate(R.color.red, ANIMATION_DURATION_SHORT, vDeviationMinus30, vDeviationPlus30)
                animate(R.color.yellow, ANIMATION_DURATION_SHORT, vDeviationMinus20, vDeviationPlus20)
                animate(R.color.yellow, ANIMATION_DURATION_SHORT, vDeviationMinus10, vDeviationPlus10)
                animate(R.color.green, ANIMATION_DURATION_LONG, vDeviationZero)
                animate(R.color.yellow, ANIMATION_DURATION_SHORT, vDeviationMinus10, vDeviationPlus10)
                animate(R.color.yellow, ANIMATION_DURATION_SHORT, vDeviationMinus20, vDeviationPlus20)
                animate(R.color.red, ANIMATION_DURATION_SHORT, vDeviationMinus30, vDeviationPlus30)
                animate(R.color.red, ANIMATION_DURATION_SHORT, vDeviationMinus40, vDeviationPlus40)
                animate(R.color.red, ANIMATION_DURATION_SHORT, vDeviationMinus50, vDeviationPlus50)

                resetBars()
            }
    }
}
