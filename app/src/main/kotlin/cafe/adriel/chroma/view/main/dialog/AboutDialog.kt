package cafe.adriel.nomanswallpaper.view.main.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import cafe.adriel.chroma.App
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.open
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.dialog_about.*

class AboutDialog private constructor(context: Context) : AppCompatDialog(context) {

    companion object {
        fun show(context: Context) = AboutDialog(
            context
        ).show()
    }

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
        vAppVersion.text =
                "${context.getString(R.string.app_name)} v${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"

        vClose.setOnClickListener { dismiss() }
        vEmail.setOnClickListener {
            sendEmail()
        }
        vWebsite.setOnClickListener {
            Uri.parse(App.WEBSITE).open(context)
        }
        vGitHub.setOnClickListener {
            Uri.parse(App.GITHUB_PROFILE_URL).open(context)
        }
        vLinkedIn.setOnClickListener {
            Uri.parse(App.LINKEDIN_PROFILE_URL).open(context)
        }
        vProjectRepo.setOnClickListener {
            Uri.parse(App.PROJECT_REPO_URL).open(context)
        }
    }

    private fun sendEmail() {
        try {
            val email = Uri.parse("mailto:${App.EMAIL}")
            val subject =
                "${context.getString(R.string.app_name)} for Android | v${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE}), SDK ${Build.VERSION.SDK_INT}"
            Intent(Intent.ACTION_SENDTO, email).run {
                putExtra(Intent.EXTRA_SUBJECT, subject)
                context.startActivity(this)
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            e.printStackTrace()
            Toast.makeText(context, "Oops! No Email app found :/", Toast.LENGTH_LONG).show()
        }
    }

}