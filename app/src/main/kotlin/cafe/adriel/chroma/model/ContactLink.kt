package cafe.adriel.chroma.model

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import cafe.adriel.chroma.R

enum class ContactLink(val url: Uri, @DrawableRes val iconRes: Int) {
    EMAIL("mailto:me@adriel.cafe".toUri(), R.drawable.ic_email),
    WEBSITE("http://adriel.cafe".toUri(), R.drawable.ic_website),
    GITHUB_PROFILE("https://github.com/adrielcafe".toUri(), R.drawable.ic_github),
    LINKEDIN_PROFILE("https://linkedin.com/in/adrielcafe".toUri(), R.drawable.ic_linkedin),
    PROJECT_REPO("https://github.com/adrielcafe/chroma".toUri(), R.drawable.ic_github)
}
