package cafe.adriel.chroma.ktx

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.R
import cafe.adriel.chroma.model.settings.AppLink
import cafe.adriel.chroma.model.settings.ContactLink
import com.google.firebase.crashlytics.FirebaseCrashlytics

val Context.appVersion: String
    get() = "${getString(R.string.app_name)} v${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.shareMessage(message: String) =
    ShareCompat
        .IntentBuilder(this)
        .setText(message)
        .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        .startChooser()

fun Context.shareApp() =
    shareMessage("${getString(R.string.you_should_try)}\n${AppLink.PLAY_STORE.url}")

fun Context.rateApp() =
    try {
        Uri.parse(AppLink.MARKET.url).open(this)
    } catch (e: Exception) {
        Uri.parse(AppLink.PLAY_STORE.url).open(this)
    }

fun Context.sendContactEmail() =
    try {
        val email = Uri.parse("mailto:${ContactLink.EMAIL.url}")
        val subject = appVersion + " SDK ${Build.VERSION.SDK_INT}"
        Intent(Intent.ACTION_SENDTO, email).run {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(this)
        }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        Toast.makeText(this, "Oops! No Email app found :/", Toast.LENGTH_LONG).show()
    }

fun Context.openExternalAppSettings() =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.fromParts("package", packageName, null))
        .let(::startActivity)
