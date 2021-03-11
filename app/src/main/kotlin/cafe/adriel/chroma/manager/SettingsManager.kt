package cafe.adriel.chroma.manager

import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.model.settings.Settings
import cafe.adriel.satchel.SatchelStorage
import cafe.adriel.satchel.ktx.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(
    storage: SatchelStorage,
    scope: CoroutineScope
) {

    private val _state by lazy { MutableStateFlow(settings) }
    val state by lazy { _state.asStateFlow() }

    var tunerBasicMode by storage.value("tuner_basic_mode", false)
    var tunerNoiseSuppressor by storage.value("tuner_noise_suppressor", false)
    var tunerSolfegeNotation by storage.value("tuner_solfege_notation", false)
    var tunerFlatSymbol by storage.value("tuner_flat_symbol", false)
    var tunerDeviationPrecisionOffset by storage.value("tuner_deviation_precision_offset", 3)
    var tunerPitchAlgorithm by storage.value("tuner_pitch_algorithm", PitchProcessor.PitchEstimationAlgorithm.FFT_YIN)

    val settings: Settings
        get() = Settings(
            basicMode = tunerBasicMode,
            noiseSuppressor = tunerNoiseSuppressor,
            solfegeNotation = tunerSolfegeNotation,
            flatSymbol = tunerFlatSymbol,
            deviationPrecisionOffset = tunerDeviationPrecisionOffset,
            pitchAlgorithm = tunerPitchAlgorithm
        )

    init {
        storage.addListener(scope) {
            _state.value = settings
        }
    }
}
