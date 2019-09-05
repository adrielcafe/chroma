package cafe.adriel.chroma.view.main.tuner

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.model.ChromaticScale
import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

class TunerManager {

    companion object {
        private const val SAMPLE_RATE = 22050
        private const val SAMPLE_RATE_BITS = 16
        private const val CHANNEL_COUNT = 1
        private const val OVERLAP = 0
        private const val BUFFER_SIZE = 2048
    }

    var listener: TunerListener? = null

    private var audioDispatcher: AudioDispatcher? = null
    private var pitchProcessor: PitchProcessor? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    private val pitchHandler by lazy {
        PitchDetectionHandler { result, _ ->
            if (result.pitch >= 0) {
                val tuning = getTuning(result.pitch)
                listener?.onTuningDetected(tuning)
            }
        }
    }

    suspend fun startListening(settings: Settings) = withContext(Dispatchers.IO) {
        try {
            val bufferSize = getBufferSize()
            val audioRecord = getAudioRecord(bufferSize).apply {
                startRecording()
            }

            startNoiseSuppressor(settings, audioRecord.audioSessionId)

            pitchProcessor = PitchProcessor(
                settings.pitchAlgorithm,
                SAMPLE_RATE.toFloat(),
                bufferSize,
                pitchHandler
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

    private fun startNoiseSuppressor(settings: Settings, audioSessionId: Int) {
        if (NoiseSuppressor.isAvailable() && settings.noiseSuppressor) {
            noiseSuppressor = NoiseSuppressor.create(audioSessionId).apply { enabled = true }
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
            val deviation = getTuningDeviation(note, detectedFrequency)
            if (abs(deviation) < abs(minDeviation)) {
                minDeviation = deviation
                closestNote = note
            }
        }

        return Tuning(closestNote, detectedFrequency, minDeviation.roundToInt())
    }

    private fun getTuningDeviation(note: ChromaticScale, detectedFrequency: Float) =
        1200 * log2(detectedFrequency / note.frequency)

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
