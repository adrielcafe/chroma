package cafe.adriel.chroma.model

import android.os.Parcelable
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings(
    val basicMode: Boolean = false,
    val noiseSuppressor: Boolean = false,
    val solfegeNotation: Boolean = false,
    val flatSymbol: Boolean = false,
    val precision: Int = 3,
    val pitchAlgorithm: PitchEstimationAlgorithm = PitchEstimationAlgorithm.FFT_YIN
) : Parcelable
