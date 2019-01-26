package cafe.adriel.chroma

import android.app.Application
import androidx.preference.PreferenceManager
import com.github.ajalt.timberkt.Timber

class App: Application() {

    companion object {
        // Contact Links
        const val EMAIL = "me@adriel.cafe"
        const val WEBSITE = "http://adriel.cafe"
        const val GITHUB_PROFILE_URL = "https://github.com/adrielcafe"
        const val LINKEDIN_PROFILE_URL = "https://linkedin.com/in/adrielcafe"
        const val PROJECT_REPO_URL = "https://github.com/adrielcafe/ChromaAndroidApp"

        // App Links
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        const val MARKET_URL = "market://details?id=${BuildConfig.APPLICATION_ID}"

        // In-App Purchase
        const val PRODUCT_SKU_COFFEE_1 = "coffee_1"
        const val PRODUCT_SKU_COFFEE_3 = "coffee_3"
        const val PRODUCT_SKU_COFFEE_5 = "coffee_5"
    }

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.RELEASE) {
            Timber.plant(Timber.DebugTree())
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

}