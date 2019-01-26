package cafe.adriel.chroma.view.main.tuner

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
import cafe.adriel.chroma.util.CoroutineScopedStateViewModel
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

class TunerViewModel(app: Application) : CoroutineScopedStateViewModel<TunerViewState>(app), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var audioDispatcher: AudioDispatcher
    private val pitchHandler by lazy {
        PitchDetectionHandler { result, _ ->
            try {
                if(result.pitch >= 0) {
                    launch {
                        updateState { it.copy(tuning = getTuning(result.pitch)) }
                    }
                }
            } catch (e: Exception){
                Crashlytics.logException(e)
                e.printStackTrace()
            }
        }
    }

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        initState { TunerViewState(Tuning(), getSettings()) }
    }

    override fun onCleared() {
        super.onCleared()
        PreferenceManager.getDefaultSharedPreferences(app).unregisterOnSharedPreferenceChangeListener(this)
        stopListening()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updateState { it.copy(settings = getSettings()) }
        startListening()
    }

    fun startListening(){
        stopListening()

        val pitchAlgorithm = getSettings().pitchAlgorithm
        val pitchProcessor = PitchProcessor(pitchAlgorithm, 22050f, 2048, pitchHandler)
        audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 2048, 0)
        audioDispatcher.addAudioProcessor(pitchProcessor)
        launch {
            withContext(Dispatchers.IO) {
                audioDispatcher.run()
            }
        }
    }

    fun stopListening(){
        if(::audioDispatcher.isInitialized) {
            try {
                audioDispatcher.stop()
            } catch (e: Exception){
                Crashlytics.logException(e)
                e.printStackTrace()
            }
        }
    }

    private fun getTuning(frequency: Float): Tuning {
        var minDeviation = Float.POSITIVE_INFINITY
        var closestNote = ChromaticScale.notes[0]
        for (note in ChromaticScale.notes) {
            val deviation = 1200 * log2(frequency / note.frequency)
            if (abs(deviation) < abs(minDeviation)) {
                minDeviation = deviation
                closestNote = note
            }
        }
        return Tuning(closestNote, frequency, minDeviation.roundToInt())
    }

    private fun getSettings(): Settings {
        val preferences = PreferenceManager.getDefaultSharedPreferences(app)
        val basicMode = preferences.getBoolean(Settings.TUNER_BASIC_MODE, false)
        val solfegeNotation = preferences.getString(Settings.TUNER_NOTATION, "0")!!.toInt() == 1
        val flatSymbol = preferences.getString(Settings.TUNER_SHARP_FLAT, "0")!!.toInt() == 1
        val precision = preferences.getString(Settings.TUNER_PRECISION, "2")!!.toInt()
        val pitchAlgorithm = when(preferences.getString(Settings.TUNER_PITCH_ALGORITHM, "0")!!.toInt()){
            1 -> PitchProcessor.PitchEstimationAlgorithm.FFT_YIN
            2 -> PitchProcessor.PitchEstimationAlgorithm.MPM
            3 -> PitchProcessor.PitchEstimationAlgorithm.AMDF
            4 -> PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET
            else -> PitchProcessor.PitchEstimationAlgorithm.YIN
        }
        return Settings(basicMode, solfegeNotation, flatSymbol, precision, pitchAlgorithm)
    }

}