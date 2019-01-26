package cafe.adriel.chroma.model

import android.os.Parcelable
import be.tarsos.dsp.pitch.PitchProcessor
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings(val basicMode: Boolean,
                    val solfegeNotation: Boolean,
                    val flatSymbol: Boolean,
                    val precision: Int,
                    val pitchAlgorithm: PitchProcessor.PitchEstimationAlgorithm) : Parcelable {

    companion object {
        const val TUNER_BASIC_MODE = "tuner_basic_mode"
        const val TUNER_NOTATION = "tuner_notation"
        const val TUNER_SHARP_FLAT = "tuner_sharp_flat"
        const val TUNER_PRECISION = "tuner_precision"
        const val TUNER_PITCH_ALGORITHM = "tuner_pitch_algorithm"
    }

}