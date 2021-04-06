package cafe.adriel.chroma.ktx

import android.app.Activity
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.R
import cafe.adriel.chroma.model.AppLink
import cafe.adriel.chroma.model.ContactLink

val Context.appVersion: String
    get() = "${getString(R.string.app_name)} v${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"

fun Context.hasPermission(permission: String): Boolean =
    runCatching {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

fun Context.shareApp() {
    runCatching {
        val message = "${getString(R.string.you_should_try)}\n${AppLink.PLAY_STORE.url}"
        ShareCompat
            .IntentBuilder(this)
            .setText(message)
            .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            .startChooser()
    }.onFailure(::logError)
}

fun Context.rateApp() {
    runCatching { Uri.parse(AppLink.MARKET.url).open(this) }
        .recover { Uri.parse(AppLink.PLAY_STORE.url).open(this) }
        .onFailure(::logError)
}

fun Context.sendContactEmail() {
    runCatching {
        val subject = "$appVersion | SDK ${Build.VERSION.SDK_INT}"
        Intent(Intent.ACTION_SENDTO, ContactLink.EMAIL.url).run {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(this)
        }
    }.onFailure(::logError)
}

fun Context.openExternalAppSettings() {
    runCatching {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", packageName, null))
            .let(::startActivity)
    }.onFailure(::logError)
}

fun Activity.keepScreenOn() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
