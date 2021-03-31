package cafe.adriel.chroma.model.settings

import cafe.adriel.chroma.model.settings.options.AccidentalOption
import cafe.adriel.chroma.model.settings.options.DeviationPrecisionOption
import cafe.adriel.chroma.model.settings.options.NotationOption
import cafe.adriel.chroma.model.settings.options.PitchDetectionAlgorithmOption

data class Settings(
    val advancedMode: Boolean,
    val noiseSuppressor: Boolean,
    val notation: NotationOption,
    val accidental: AccidentalOption,
    val pitchDetectionAlgorithm: PitchDetectionAlgorithmOption,
    val deviationPrecision: DeviationPrecisionOption
)
