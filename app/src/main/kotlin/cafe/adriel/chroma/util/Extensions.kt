package cafe.adriel.chroma.util

import android.app.Activity
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.app.ShareCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import cafe.adriel.chroma.R
import com.crashlytics.android.Crashlytics

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.color(@ColorRes resId: Int) = ResourcesCompat.getColor(resources, resId, theme)
fun View.color(@ColorRes resId: Int) = context.color(resId)
fun Fragment.color(@ColorRes resId: Int) = context?.color(resId) ?: Color.TRANSPARENT

fun String.share(activity: Activity) =
    ShareCompat.IntentBuilder
        .from(activity)
        .setText(this)
        .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        .startChooser()

fun Uri.open(context: Context, showErrorMessage: Boolean = true) = try {
    context.startActivity(Intent(Intent.ACTION_VIEW, this))
} catch (e: Exception) {
    if(showErrorMessage) {
        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
    }
    Crashlytics.logException(e)
    e.printStackTrace()
}

fun getDeviationColorRes(deviation: Int, precision: Int) =
    if(deviation in -precision..precision) R.color.green
    else if(deviation in -20..-precision || deviation in precision..20) R.color.yellow
    else R.color.red