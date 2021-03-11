package cafe.adriel.chroma.model.settings

import android.os.Parcelable
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings(
    val basicMode: Boolean,
    val noiseSuppressor: Boolean,
    val solfegeNotation: Boolean,
    val flatSymbol: Boolean,
    val deviationPrecisionOffset: Int,
    val pitchAlgorithm: PitchEstimationAlgorithm
) : Parcelable
