package cafe.adriel.chroma.view.main.tuner

import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import cafe.adriel.hal.HAL

sealed class TunerAction : HAL.Action {
    data class TuningDetected(val tuning: Tuning) : TunerAction()
    data class TuningDetectionFailed(val error: Exception) : TunerAction()
    object SettingsChanged : TunerAction()
}

data class TunerState(
    val tuning: Tuning = Tuning(),
    val settings: Settings = Settings(),
    val error: Exception? = null
) : HAL.State
