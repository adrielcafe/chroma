package cafe.adriel.chroma.view.main.tuner

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import cafe.adriel.chroma.R
import cafe.adriel.chroma.model.ChromaticScale
import cafe.adriel.chroma.util.color
import cafe.adriel.chroma.util.getDeviationColorRes
import cafe.adriel.chroma.util.hasPermission
import cafe.adriel.chroma.util.showToast
import cafe.adriel.chroma.view.BaseFragment
import com.etiennelenhart.eiffel.state.peek
import com.google.android.material.snackbar.Snackbar
import com.markodevcic.peko.Peko
import com.markodevcic.peko.rationale.SnackBarRationale
import kotlinx.android.synthetic.main.fragment_tuner.*
import kotlinx.coroutines.launch
import org.rewedigital.katana.androidx.viewmodel.viewModel

class TunerFragment : BaseFragment<TunerViewState>() {

    companion object {
        private const val FREQUENCY_FORMAT = "%.2f"
    }

    override val viewModel by viewModel<TunerViewModel, TunerFragment>()

    private val tuningViews by lazy {
        listOf(
            vStandardFrequency, vFrequency, vOctave, vDeviation, vStandardFrequencyUnit, vFrequencyUnit, vDeviationUnit
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_tuner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        startListening()
    }

    override fun onPause() {
        super.onPause()
        stopListening()
    }

    override fun onStateUpdated(state: TunerViewState) {
        state.apply {
            tuning.note?.let {
                updateViewsVisibility(settings.basicMode)
                updateTone(tuning.note.tone, tuning.note.semitone, settings.flatSymbol, settings.solfegeNotation)
                updateSemitone(tuning.note.semitone, settings.flatSymbol)
                updateOctave(tuning.note.octave)
                updateFrequency(tuning.note.frequency, tuning.frequency)
                updateDeviation(tuning.deviation, settings.precision)
            }

            exception?.let {
                showToast("ERROR: ${it.message}")
            }

            event?.peek {
                when (it) {
                    is TunerViewEvent.SettingsChangedEvent -> {
                        viewModel.startListening()
                        true
                    }
                }
            }
        }
    }

    private fun init() {
        vGivePermission.setOnClickListener {
            showExternalAppSettings()
            it.visibility = View.GONE
        }

        lifecycleScope.launch {
            requestPermission()
        }
    }

    private fun updateViewsVisibility(basicMode: Boolean) {
        vMakeNoise.visibility = View.GONE
        vGivePermission.visibility = View.GONE

        val visibility = if (basicMode) View.GONE else View.VISIBLE
        tuningViews.forEach { it.visibility = visibility }
    }

    private fun updateTone(tone: String, semitone: Boolean, flatSymbol: Boolean, solfegeNotation: Boolean) {
        vTone.text = when {
            flatSymbol && solfegeNotation && semitone -> ChromaticScale.getSolfegeTone(ChromaticScale.getFlatTone(tone))
            flatSymbol && semitone -> ChromaticScale.getFlatTone(tone)
            solfegeNotation -> ChromaticScale.getSolfegeTone(tone)
            else -> tone
        }
    }

    private fun updateSemitone(semitone: Boolean, flatSymbol: Boolean) {
        vSemitone.text = when {
            semitone -> getString(if (flatSymbol) R.string.flat_symbol else R.string.sharp_symbol)
            else -> ""
        }
    }

    private fun updateOctave(octave: Int) {
        vOctave.text = octave.toString()
    }

    private fun updateFrequency(standardFrequency: Float, detectedFrequency: Float) {
        if (vStandardFrequencyUnit.text.isNullOrBlank())
            vStandardFrequencyUnit.text = getString(R.string.hertz_unit)
        if (vFrequencyUnit.text.isNullOrBlank())
            vFrequencyUnit.text = getString(R.string.hertz_unit)

        vStandardFrequency.text = FREQUENCY_FORMAT.format(standardFrequency)
        vFrequency.text = FREQUENCY_FORMAT.format(detectedFrequency)
    }

    private fun updateDeviation(deviation: Int, precision: Int) {
        if (vDeviationUnit.text.isNullOrEmpty())
            vDeviationUnit.text = getString(R.string.deviation_unit)

        vDeviation.text = deviation.toString()
        vDeviationBars.deviation = deviation
        vDeviationBars.precision = precision

        vDeviation.setTextColor(color(getDeviationColorRes(deviation, precision)))
        vDeviationUnit.setTextColor(color(getDeviationColorRes(deviation, precision)))
    }

    private fun startListening() {
        if (hasPermission(Manifest.permission.RECORD_AUDIO)) {
            vGivePermission.visibility = View.GONE
            if (vMakeNoise.visibility == View.VISIBLE) {
                vDeviationBars.animateBars()
            }
            viewModel.startListening()
        } else {
            vGivePermission.visibility = View.VISIBLE
        }
    }

    private fun stopListening() {
        viewModel.stopListening()
    }

    private fun showExternalAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }
        startActivity(intent)
    }

    private suspend fun requestPermission(): Boolean {
        val snackBar = Snackbar.make(requireView(), getString(R.string.permission_needed), Snackbar.LENGTH_LONG)
        val snackBarRationale = SnackBarRationale(snackBar, getString(R.string.request_again))
        val result = Peko.requestPermissionsAsync(
            requireActivity(),
            Manifest.permission.RECORD_AUDIO,
            rationale = snackBarRationale
        )
        return Manifest.permission.RECORD_AUDIO in result.grantedPermissions
    }
}
