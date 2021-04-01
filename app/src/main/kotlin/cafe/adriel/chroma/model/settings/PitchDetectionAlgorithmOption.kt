package cafe.adriel.chroma.model.settings

import androidx.annotation.StringRes
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import cafe.adriel.chroma.R
import cafe.adriel.chroma.view.components.SelectOption

enum class PitchDetectionAlgorithmOption(
    @StringRes override val labelRes: Int,
    val algorithm: PitchEstimationAlgorithm
) : SelectOption<PitchDetectionAlgorithmOption> {
    YIN(R.string.pitch_detection_algorithm_yin, PitchEstimationAlgorithm.YIN),
    FFT_YIN(R.string.pitch_detection_algorithm_fft_yin, PitchEstimationAlgorithm.FFT_YIN),
    MPM(R.string.pitch_detection_algorithm_mpm, PitchEstimationAlgorithm.MPM),
    AMDF(R.string.pitch_detection_algorithm_amdf, PitchEstimationAlgorithm.AMDF),
    DYWA(R.string.pitch_detection_algorithm_dywa, PitchEstimationAlgorithm.DYNAMIC_WAVELET);

    companion object {
        const val titleRes = R.string.pitch_detection_algorithm
    }
}
