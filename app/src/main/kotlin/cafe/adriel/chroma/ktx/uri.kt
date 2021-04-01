package cafe.adriel.chroma.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.MailTo

fun Uri.open(context: Context) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, this))
    }
}

fun Uri.isEmail(): Boolean =
    scheme == MailTo.MAILTO_SCHEME
