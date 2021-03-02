package cafe.adriel.chroma.manager

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.model.ChromaticScale
import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import kotlin.math.absoluteValue
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TunerManager : PitchDetectionHandler {

    companion object {
        private const val SAMPLE_RATE = 22050
        private const val SAMPLE_RATE_BITS = 16
        private const val CHANNEL_COUNT = 1
        private const val OVERLAP = 0
        private const val BUFFER_SIZE = 2048
    }

    private var audioDispatcher: AudioDispatcher? = null
    private var pitchProcessor: PitchProcessor? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    var listener: TunerListener? = null

    override fun handlePitch(result: PitchDetectionResult?, event: AudioEvent?) {
        val pitch = result?.pitch ?: -1F
        if (pitch >= 0) {
            listener?.onTuningDetected(getTuning(pitch))
        }
    }

    suspend fun startListening(settings: Settings) = withContext(Dispatchers.IO) {
        try {
            val bufferSize = getBufferSize()
            val audioRecord = getAudioRecord(bufferSize).apply {
                startRecording()
            }

            if (NoiseSuppressor.isAvailable() && settings.noiseSuppressor) {
                startNoiseSuppressor(audioRecord.audioSessionId)
            }

            pitchProcessor = PitchProcessor(
                settings.pitchAlgorithm,
                SAMPLE_RATE.toFloat(),
                bufferSize,
                this@TunerManager
            )
            audioDispatcher = getAudioDispatcher(audioRecord, bufferSize).apply {
                addAudioProcessor(pitchProcessor)
                run()
            }
        } catch (e: Exception) {
            listener?.onError(e)
        }
    }

    suspend fun stopListening() = withContext(Dispatchers.IO) {
        try {
            stopNoiseSuppressor()

            audioDispatcher?.apply {
                removeAudioProcessor(pitchProcessor)
                stop()
            }

            noiseSuppressor = null
            pitchProcessor = null
            audioDispatcher = null
        } catch (e: Exception) {
            listener?.onError(e)
        }
    }

    private fun startNoiseSuppressor(audioSessionId: Int) {
        noiseSuppressor = NoiseSuppressor.create(audioSessionId).apply {
            enabled = true
        }
    }

    private fun stopNoiseSuppressor() {
        noiseSuppressor?.apply {
            enabled = false
            release()
        }
    }

    private fun getTuning(detectedFrequency: Float): Tuning {
        var minDeviation = Float.POSITIVE_INFINITY
        var closestNote = ChromaticScale.notes.first()

        ChromaticScale.notes.forEach { note ->
            val deviation = getTuningDeviation(note.frequency, detectedFrequency)
            if (deviation.absoluteValue < minDeviation.absoluteValue) {
                minDeviation = deviation
                closestNote = note
            }
        }

        return Tuning(closestNote, detectedFrequency, minDeviation.roundToInt())
    }

    private fun getTuningDeviation(standardFrequency: Float, detectedFrequency: Float) =
        1200 * log2(detectedFrequency / standardFrequency)

    private fun getAudioDispatcher(audioRecord: AudioRecord, bufferSize: Int): AudioDispatcher {
        val format = TarsosDSPAudioFormat(SAMPLE_RATE.toFloat(), SAMPLE_RATE_BITS, CHANNEL_COUNT, true, false)
        val audioStream = AndroidAudioInputStream(audioRecord, format)

        return AudioDispatcher(audioStream, bufferSize, OVERLAP)
    }

    private fun getAudioRecord(bufferSize: Int) =
        AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize * 2
        )

    private fun getBufferSize(): Int {
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val minAudioBufferSizeInSamples = minBufferSize / 2

        return if (minAudioBufferSizeInSamples > BUFFER_SIZE) minAudioBufferSizeInSamples else BUFFER_SIZE
    }

    interface TunerListener {

        fun onTuningDetected(tuning: Tuning)

        fun onError(error: Exception)
    }
}
