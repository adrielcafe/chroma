package cafe.adriel.chroma.view.main.tuner

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.App
import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import cafe.adriel.chroma.view.main.settings.SettingsFragment
import com.crashlytics.android.Crashlytics
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.rewedigital.katana.Component
import org.rewedigital.katana.KatanaTrait

class TunerViewModel(
    private val preferences: SharedPreferences,
    private val tunerManager: TunerManager
) : StateViewModel<TunerViewState>(), KatanaTrait, TunerManager.TunerListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    override val component = Component(dependsOn = listOf(App.appComponent))
    override val state = MutableLiveData<TunerViewState>()

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
        tunerManager.listener = this

        viewModelScope.launch {
            initState { TunerViewState(tuning = Tuning(), settings = getSettings()) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        preferences.unregisterOnSharedPreferenceChangeListener(this)
        tunerManager.listener = null

        stopListening()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        viewModelScope.launch {
            updateState {
                it.copy(settings = getSettings(), event = TunerViewEvent.SettingsChangedEvent)
            }
        }
    }

    override fun onTuningDetected(tuning: Tuning) {
        viewModelScope.launch {
            updateState {
                it.copy(tuning = tuning)
            }
        }
    }

    override fun onError(error: Exception) {
        Crashlytics.logException(error)
        error.printStackTrace()

        viewModelScope.launch {
            updateState {
                it.copy(exception = error)
            }
        }
    }

    fun startListening() {
        viewModelScope.launch {
            tunerManager.stopListening()
            tunerManager.startListening(getSettings())
        }
    }

    fun stopListening() {
        viewModelScope.launch {
            tunerManager.stopListening()
        }
    }

    private suspend fun getSettings() = withContext(Dispatchers.IO) {
        preferences.run {
            val noiseSuppressor = getBoolean(SettingsFragment.TUNER_NOISE_SUPPRESSOR, false)
            val basicMode = getBoolean(SettingsFragment.TUNER_BASIC_MODE, false)
            val solfegeNotation = getString(SettingsFragment.TUNER_NOTATION, "0")!!.toInt() == 1
            val flatSymbol = getString(SettingsFragment.TUNER_SHARP_FLAT, "0")!!.toInt() == 1
            val precision = getString(SettingsFragment.TUNER_PRECISION, "2")!!.toInt()
            val pitchAlgorithm = when (getString(SettingsFragment.TUNER_PITCH_ALGORITHM, "0")!!.toInt()) {
                1 -> PitchProcessor.PitchEstimationAlgorithm.FFT_YIN
                2 -> PitchProcessor.PitchEstimationAlgorithm.MPM
                3 -> PitchProcessor.PitchEstimationAlgorithm.AMDF
                4 -> PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET
                else -> PitchProcessor.PitchEstimationAlgorithm.YIN
            }

            Settings(basicMode, noiseSuppressor, solfegeNotation, flatSymbol, precision, pitchAlgorithm)
        }
    }
}
