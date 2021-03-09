package cafe.adriel.chroma.util

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cafe.adriel.chroma.R
import com.google.firebase.crashlytics.FirebaseCrashlytics

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline fun <reified T : Any> tag(): String = T::class.java.simpleName

fun Context.color(@ColorRes resId: Int) = ResourcesCompat.getColor(resources, resId, theme)
fun Fragment.color(@ColorRes resId: Int) = context?.color(resId) ?: android.graphics.Color.TRANSPARENT
fun View.color(@ColorRes resId: Int) = context.color(resId)

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()
fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    context?.showToast(message, length)

fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
fun Fragment.hasPermission(permission: String) =
    context?.hasPermission(permission) ?: false

fun Uri.open(context: Context, showErrorMessage: Boolean = true) = try {
    context.startActivity(Intent(Intent.ACTION_VIEW, this))
} catch (e: Exception) {
    if (showErrorMessage) {
        context.showToast(context.getString(R.string.something_went_wrong))
    }
    FirebaseCrashlytics.getInstance().recordException(e)
}

fun String.share(activity: FragmentActivity) =
    ShareCompat.IntentBuilder
        .from(activity)
        .setText(this)
        .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        .startChooser()

fun getDeviationColorRes(deviation: Int, precision: Int) = when (deviation) {
    in -precision..precision -> R.color.green
    in -20..-precision, in precision..20 -> R.color.yellow
    else -> R.color.red
}
