package cafe.adriel.chroma.model.tuner

sealed class TuningDeviationResult {

    object NotDetected : TuningDeviationResult()

    data class Detected(
        val value: Int,
        val precision: TuningDeviationPrecision
    ) : TuningDeviationResult()

    data class Animation(
        val negativeValue: Int,
        val negativePrecision: TuningDeviationPrecision,
        val positiveValue: Int,
        val positivePrecision: TuningDeviationPrecision
    ) : TuningDeviationResult()
}
