package cafe.adriel.chroma.view.main.tuner

import android.Manifest
import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
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
        private const val AUDIO_BUFFER_OVERLAP = 0
    }

    private var audioDispatcher: AudioDispatcher? = null
    private var pitchProcessor: PitchProcessor? = null
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
                pitchProcessor = PitchProcessor(getSettings().pitchAlgorithm, AUDIO_SAMPLE_RATE.toFloat(), AUDIO_BUFFER_SIZE, pitchHandler)
                audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(AUDIO_SAMPLE_RATE, AUDIO_BUFFER_SIZE, AUDIO_BUFFER_OVERLAP).apply{
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
            audioDispatcher?.apply {
                removeAudioProcessor(pitchProcessor)
                stop()
            }
            pitchProcessor = null
            audioDispatcher = null
        } catch (e: Exception){
            Crashlytics.logException(e)
            e.printStackTrace()
            updateState { it.copy(exception = e) }
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
        Settings(basicMode, solfegeNotation, flatSymbol, precision, pitchAlgorithm)
    }

}