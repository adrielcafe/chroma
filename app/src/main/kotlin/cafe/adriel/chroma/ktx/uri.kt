package cafe.adriel.chroma.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns

private val EMAIL_REGEX = Patterns.EMAIL_ADDRESS.toRegex()

fun Uri.open(context: Context) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, this))
    }
}

fun Uri.isEmail(): Boolean =
    EMAIL_REGEX.matches(toString())
