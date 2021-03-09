package cafe.adriel.chroma.view.main.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import cafe.adriel.chroma.App
import cafe.adriel.chroma.BuildConfig.VERSION_CODE
import cafe.adriel.chroma.BuildConfig.VERSION_NAME
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.open
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.dialog_about.*

class AboutDialog private constructor(context: Context) : AppCompatDialog(context) {

    companion object {
        fun show(context: Context) = AboutDialog(context).show()
    }

    private val appVersion =
        "${context.getString(R.string.app_name)} v$VERSION_NAME (Build $VERSION_CODE)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_about)

        val projectRepoIcon = context.getDrawable(R.drawable.ic_github).also {
            it?.setTint(Color.WHITE)
        }
        vProjectRepo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            projectRepoIcon,
            null,
            null,
            null
        )
        vAppVersion.text = appVersion

        vClose.setOnClickListener { dismiss() }
        vEmail.setOnClickListener { sendEmail() }
        vWebsite.setOnClickListener { Uri.parse(App.WEBSITE).open(context) }
        vGitHub.setOnClickListener { Uri.parse(App.GITHUB_PROFILE_URL).open(context) }
        vLinkedIn.setOnClickListener { Uri.parse(App.LINKEDIN_PROFILE_URL).open(context) }
        vProjectRepo.setOnClickListener { Uri.parse(App.PROJECT_REPO_URL).open(context) }
    }

    private fun sendEmail() {
        try {
            val email = Uri.parse("mailto:${App.EMAIL}")
            val subject = "${context.getString(R.string.app_name)} for Android | " +
                "v$VERSION_NAME (Build $VERSION_CODE), SDK $SDK_INT"
            Intent(Intent.ACTION_SENDTO, email).run {
                putExtra(Intent.EXTRA_SUBJECT, subject)
                context.startActivity(this)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Toast.makeText(context, "Oops! No Email app found :/", Toast.LENGTH_LONG).show()
        }
    }
}
