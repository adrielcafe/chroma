package cafe.adriel.chroma.view.main.tuner

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.manager.TunerManager
import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import cafe.adriel.chroma.view.main.settings.SettingsFragment
import cafe.adriel.hal.HAL
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

class TunerViewModel(
    private val preferences: SharedPreferences,
    private val tunerManager: TunerManager
) : ViewModel(),
    HAL.StateMachine<TunerAction, TunerState>,
    TunerManager.TunerListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    override val stateMachine by HAL(TunerState(settings = getSettings()), viewModelScope) { action, state ->
        when (action) {
            is TunerAction.TuningDetected -> +state.copy(tuning = action.tuning, error = null)
            is TunerAction.TuningDetectionFailed -> +state.copy(tuning = Tuning(), error = action.error)
            is TunerAction.SettingsChanged -> +state.copy(settings = getSettings(), error = null)
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
        tunerManager.listener = this
    }

    override fun onCleared() {
        super.onCleared()
        preferences.unregisterOnSharedPreferenceChangeListener(this)
        tunerManager.listener = null

        stopListening()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        stateMachine.emit(TunerAction.SettingsChanged)
    }

    override fun onTuningDetected(tuning: Tuning) {
        stateMachine.emit(TunerAction.TuningDetected(tuning))
    }

    override fun onError(error: Exception) {
        FirebaseCrashlytics.getInstance().recordException(error)
        error.printStackTrace()

        stateMachine.emit(TunerAction.TuningDetectionFailed(error))
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

    private fun getSettings() =
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
