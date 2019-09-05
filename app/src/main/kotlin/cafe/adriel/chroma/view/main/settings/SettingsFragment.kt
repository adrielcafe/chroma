package cafe.adriel.chroma.view.main.settings

import android.graphics.Color
import android.media.audiofx.NoiseSuppressor
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import androidx.preference.forEach
import androidx.preference.get
import androidx.recyclerview.widget.RecyclerView
import cafe.adriel.chroma.App
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.open
import cafe.adriel.chroma.util.share
import cafe.adriel.chroma.view.main.dialog.AboutDialog
import cafe.adriel.chroma.view.main.dialog.DonateDialog

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val TUNER_BASIC_MODE = "tuner_basic_mode"
        const val TUNER_NOISE_SUPPRESSOR = "tuner_noise_suppressor"
        const val TUNER_NOTATION = "tuner_notation"
        const val TUNER_SHARP_FLAT = "tuner_sharp_flat"
        const val TUNER_PRECISION = "tuner_precision"
        const val TUNER_PITCH_ALGORITHM = "tuner_pitch_algorithm"

        const val ABOUT_ABOUT = "about_about"
        const val ABOUT_BUY_ME_COFFEE = "about_buy_me_coffee"
        const val ABOUT_SHARE = "about_share"
        const val ABOUT_RATE_REVIEW = "about_rate_review"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        if (!NoiseSuppressor.isAvailable()) {
            preferenceScreen.get<Preference>(TUNER_NOISE_SUPPRESSOR)?.isVisible = false
        }

        findPreference<Preference>(ABOUT_ABOUT)?.setOnPreferenceClickListener {
            AboutDialog.show(requireContext())
            true
        }
        findPreference<Preference>(ABOUT_BUY_ME_COFFEE)?.setOnPreferenceClickListener {
            DonateDialog.show(requireContext())
            true
        }
        findPreference<Preference>(ABOUT_SHARE)?.setOnPreferenceClickListener {
            shareApp()
            true
        }
        findPreference<Preference>(ABOUT_RATE_REVIEW)?.setOnPreferenceClickListener {
            rateApp()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.recycler_view)
            ?.isVerticalScrollBarEnabled = false
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen?) {
        if (preferenceScreen != null) updatePreferenceIcon(preferenceScreen)
        super.setPreferenceScreen(preferenceScreen)
    }

    fun setBillingSupported(supported: Boolean) {
        findPreference<Preference>(ABOUT_BUY_ME_COFFEE)?.isVisible = supported
    }

    private fun updatePreferenceIcon(preference: Preference) {
        preference.isIconSpaceReserved = false
        if (preference is PreferenceGroup) {
            preference.forEach {
                it.icon?.setTint(Color.WHITE)
                updatePreferenceIcon(it)
            }
        }
    }

    private fun shareApp() {
        "${getString(R.string.you_should_try)}\n${App.PLAY_STORE_URL}".share(requireActivity())
    }

    private fun rateApp() {
        try {
            Uri.parse(App.MARKET_URL).open(requireContext())
        } catch (e: Exception) {
            Uri.parse(App.PLAY_STORE_URL).open(requireContext())
        }
    }
}
