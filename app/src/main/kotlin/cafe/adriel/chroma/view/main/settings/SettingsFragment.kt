package cafe.adriel.chroma.view.main.settings

import android.os.Bundle
import androidx.preference.*
import cafe.adriel.chroma.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
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