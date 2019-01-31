package cafe.adriel.chroma.model

import android.os.Parcelable
import be.tarsos.dsp.pitch.PitchProcessor
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings(val basicMode: Boolean,
                    val noiseSuppressor: Boolean,
                    val solfegeNotation: Boolean,
                    val flatSymbol: Boolean,
                    val precision: Int,
                    val pitchAlgorithm: PitchProcessor.PitchEstimationAlgorithm) : Parcelable