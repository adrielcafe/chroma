package cafe.adriel.chroma.view.main.settings

import android.media.audiofx.NoiseSuppressor
import android.os.Bundle
import androidx.preference.*
import cafe.adriel.chroma.R
import cafe.adriel.chroma.model.Settings

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        if(!NoiseSuppressor.isAvailable()) {
            preferenceScreen[Settings.TUNER_NOISE_SUPPRESSOR].isVisible = false
        }
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen?) {
        if (preferenceScreen != null) hideIcon(preferenceScreen)
        super.setPreferenceScreen(preferenceScreen)
    }

    private fun hideIcon(preference: Preference) {
        preference.isIconSpaceReserved = false
        if (preference is PreferenceGroup) {
            preference.forEach { hideIcon(it) }
        }
    }

}