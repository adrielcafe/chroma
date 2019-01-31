package cafe.adriel.chroma.view.main.tuner

import android.Manifest
import android.app.Application
import android.content.SharedPreferences
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import androidx.preference.PreferenceManager
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import cafe.adriel.chroma.model.ChromaticScale
import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import cafe.adriel.chroma.util.StateAndroidViewModel
import cafe.adriel.chroma.util.hasPermission
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

class TunerViewModel(app: Application) : StateAndroidViewModel<TunerViewState>(app), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val AUDIO_SAMPLE_RATE = 22050
        private const val AUDIO_BUFFER_SIZE = 2048
    }

    private var audioSessionId: Int = -1
    private var audioDispatcher: AudioDispatcher? = null
    private var pitchProcessor: PitchProcessor? = null
    private var noiseSuppressor: NoiseSuppressor? = null
    private val pitchHandler by lazy {
        PitchDetectionHandler { result, _ ->
            try {
                if (result.pitch >= 0) {
                    launch {
                        updateState { it.copy(tuning = getTuning(result.pitch)) }
                    }
                }
            } catch (e: Exception) {
                Crashlytics.logException(e)
                e.printStackTrace()
                updateState { it.copy(exception = e) }
            }
        }
    }

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        launch {
            initState { TunerViewState(Tuning(), getSettings()) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        PreferenceManager.getDefaultSharedPreferences(app).unregisterOnSharedPreferenceChangeListener(this)
        stopListening()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        launch {
            updateState { it.copy(settings = getSettings()) }
            if(app.hasPermission(Manifest.permission.RECORD_AUDIO)) {
                startListening()
            }
        }
    }

    fun startListening() {
        stopListening()
        try {
            launch {
                val settings = getSettings()
                pitchProcessor = PitchProcessor(settings.pitchAlgorithm, AUDIO_SAMPLE_RATE.toFloat(), getBufferSize(), pitchHandler)
                audioDispatcher = getAudioDispatcher().apply {
                    startNoiseSuppressor(settings)
                    addAudioProcessor(pitchProcessor)
                    Thread(this, "Pitch Tracker").start()
                }
            }
        } catch (e: Exception){
            Crashlytics.logException(e)
            e.printStackTrace()
            updateState { it.copy(exception = e) }
        }
    }

    fun stopListening() {
        try {
            stopNoiseSuppressor()
            audioDispatcher?.apply {
                removeAudioProcessor(pitchProcessor)
                stop()
            }
            noiseSuppressor = null
            pitchProcessor = null
            audioDispatcher = null
        } catch (e: Exception){
            Crashlytics.logException(e)
            e.printStackTrace()
            updateState { it.copy(exception = e) }
        }
    }

    private fun startNoiseSuppressor(settings: Settings){
        if(NoiseSuppressor.isAvailable() && settings.noiseSuppressor) {
            noiseSuppressor = NoiseSuppressor.create(audioSessionId)
                .apply { enabled = true }
        }
    }

    private fun stopNoiseSuppressor(){
        noiseSuppressor?.apply {
            enabled = false
            release()
        }
    }

    private suspend fun getTuning(frequency: Float) = withContext(Dispatchers.Default) {
        var minDeviation = Float.POSITIVE_INFINITY
        var closestNote = ChromaticScale.notes[0]
        for (note in ChromaticScale.notes) {
            val deviation = 1200 * log2(frequency / note.frequency)
            if (abs(deviation) < abs(minDeviation)) {
                minDeviation = deviation
                closestNote = note
            }
        }
        Tuning(closestNote, frequency, minDeviation.roundToInt())
    }

    private suspend fun getSettings() = withContext(Dispatchers.IO) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(app)
        val noiseSuppressor = preferences.getBoolean(Settings.TUNER_NOISE_SUPPRESSOR, false)
        val basicMode = preferences.getBoolean(Settings.TUNER_BASIC_MODE, false)
        val solfegeNotation = preferences.getString(Settings.TUNER_NOTATION, "0")!!.toInt() == 1
        val flatSymbol = preferences.getString(Settings.TUNER_SHARP_FLAT, "0")!!.toInt() == 1
        val precision = preferences.getString(Settings.TUNER_PRECISION, "2")!!.toInt()
        val pitchAlgorithm = when (preferences.getString(Settings.TUNER_PITCH_ALGORITHM, "0")!!.toInt()) {
            1 -> PitchProcessor.PitchEstimationAlgorithm.FFT_YIN
            2 -> PitchProcessor.PitchEstimationAlgorithm.MPM
            3 -> PitchProcessor.PitchEstimationAlgorithm.AMDF
            4 -> PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET
            else -> PitchProcessor.PitchEstimationAlgorithm.YIN
        }
        Settings(basicMode, noiseSuppressor, solfegeNotation, flatSymbol, precision, pitchAlgorithm)
    }

    private suspend fun getAudioDispatcher() = withContext(Dispatchers.IO) {
        val bufferSize = getBufferSize()
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE,
            android.media.AudioFormat.CHANNEL_IN_MONO,
            android.media.AudioFormat.ENCODING_PCM_16BIT,
            bufferSize * 2
        )
        val format = TarsosDSPAudioFormat(AUDIO_SAMPLE_RATE.toFloat(), 16, 1, true, false)
        val audioStream = AndroidAudioInputStream(audioRecord, format)
        audioRecord.startRecording()
        audioSessionId = audioRecord.audioSessionId
        AudioDispatcher(audioStream, bufferSize, 0)
    }

    private suspend fun getBufferSize() = withContext(Dispatchers.Default) {
        val minBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val minAudioBufferSizeInSamples = minBufferSize / 2
        if(minAudioBufferSizeInSamples > AUDIO_BUFFER_SIZE)
            minAudioBufferSizeInSamples
        else
            AUDIO_BUFFER_SIZE
    }

}