package cafe.adriel.chroma.util

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import cafe.adriel.chroma.R
import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Uri.open(context: Context, showErrorMessage: Boolean = true) = try {
    context.startActivity(Intent(Intent.ACTION_VIEW, this))
} catch (e: Exception) {
    if (showErrorMessage) {
        context.showToast(context.getString(R.string.something_went_wrong))
    }
    FirebaseCrashlytics.getInstance().recordException(e)
}

fun String.share(activity: FragmentActivity) =
    ShareCompat
        .IntentBuilder(activity)
        .setText(this)
        .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        .startChooser()
