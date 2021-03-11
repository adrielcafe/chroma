package cafe.adriel.chroma.manager

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.model.settings.Settings
import cafe.adriel.chroma.model.tuner.ChromaticScale
import cafe.adriel.chroma.model.tuner.Tuning
import cafe.adriel.chroma.model.tuner.TuningDeviationPrecision
import cafe.adriel.chroma.model.tuner.TuningDeviationResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlin.math.absoluteValue
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TunerManager(
    private val settingsManager: SettingsManager,
    private val permissionManager: PermissionManager,
    private val lifecycleOwner: LifecycleOwner
) : LifecycleEventObserver, PitchDetectionHandler {

    private companion object {
        const val SAMPLE_RATE = 22050
        const val SAMPLE_RATE_BITS = 16
        const val CHANNEL_COUNT = 1
        const val OVERLAP = 0
        const val BUFFER_SIZE = 2048
    }

    private val _state by lazy { MutableStateFlow(Tuning()) }
    val state by lazy { _state.asStateFlow() }

    private var audioDispatcher: AudioDispatcher? = null
    private var pitchProcessor: PitchProcessor? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun handlePitch(result: PitchDetectionResult?, event: AudioEvent?) {
        val pitch = result?.pitch ?: -1F
        if (pitch >= 0) {
            _state.value = getTuning(pitch)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        lifecycleOwner.lifecycleScope.launch {
            when (event) {
                Lifecycle.Event.ON_START -> startListener(settingsManager.settings)
                Lifecycle.Event.ON_STOP -> stopListener()
            }
        }
    }

    fun restartListener() {
        lifecycleOwner.lifecycleScope.launch {
            stopListener()
            startListener(settingsManager.settings)
        }
    }

    private suspend fun startListener(settings: Settings) = withContext(Dispatchers.IO) {
        try {
            if (permissionManager.hasRequiredPermissions.not()) {
                return@withContext
            }

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
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun stopListener() = withContext(Dispatchers.IO) {
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
            FirebaseCrashlytics.getInstance().recordException(e)
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
        var minDeviation = Int.MAX_VALUE
        var closestNote = ChromaticScale.notes.first()

        ChromaticScale.notes.forEach { note ->
            val deviation = getTuningDeviation(note.frequency, detectedFrequency)
            if (deviation.absoluteValue < minDeviation.absoluteValue) {
                minDeviation = deviation
                closestNote = note
            }
        }

        val deviationResult = TuningDeviationResult.Detected(
            value = minDeviation,
            precision = TuningDeviationPrecision.fromDeviation(
                deviation = minDeviation,
                offset = settingsManager.tunerDeviationPrecisionOffset
            )
        )

        return Tuning(closestNote, detectedFrequency, deviationResult)
    }

    private fun getTuningDeviation(standardFrequency: Float, detectedFrequency: Float) =
        (1200 * log2(detectedFrequency / standardFrequency)).roundToInt()

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
}
