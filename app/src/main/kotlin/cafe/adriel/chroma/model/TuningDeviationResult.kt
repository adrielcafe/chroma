package cafe.adriel.chroma.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class TuningDeviationResult : Parcelable {

    @Parcelize
    object NotDetected : TuningDeviationResult()

    @Parcelize
    data class Detected(
        val value: Int,
        val precision: TuningDeviationPrecision
    ) : TuningDeviationResult()

    @Parcelize
    data class Animation(
        val negativeValue: Int,
        val negativePrecision: TuningDeviationPrecision,
        val positiveValue: Int,
        val positivePrecision: TuningDeviationPrecision
    ) : TuningDeviationResult()
}
