package cafe.adriel.chroma.model.settings

data class Settings(
    val advancedMode: Boolean,
    val noiseSuppressor: Boolean,
    val notation: NotationOption,
    val accidental: AccidentalOption,
    val pitchDetectionAlgorithm: PitchDetectionAlgorithmOption,
    val deviationPrecision: DeviationPrecisionOption
)
