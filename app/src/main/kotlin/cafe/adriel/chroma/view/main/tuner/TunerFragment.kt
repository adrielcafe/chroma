package cafe.adriel.chroma.view.main.tuner

import android.Manifest
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cafe.adriel.chroma.R
import cafe.adriel.chroma.model.ChromaticScale
import cafe.adriel.chroma.util.CoroutineScopedStateViewModelFactory
import cafe.adriel.chroma.util.color
import cafe.adriel.chroma.util.getDeviationColorRes
import cafe.adriel.chroma.view.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.markodevcic.peko.Peko
import com.markodevcic.peko.rationale.SnackBarRationale
import kotlinx.android.synthetic.main.fragment_tuner.*
import kotlinx.coroutines.launch

class TunerFragment: BaseFragment<TunerViewState>() {

    companion object {
        private const val REQUEST_PERMISSION_SETTING = 0
    }

    private val viewModel by lazy {
        CoroutineScopedStateViewModelFactory.getInstance(context?.applicationContext as Application).create(TunerViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(cafe.adriel.chroma.R.layout.fragment_tuner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState(this, ::onStateUpdated)
        startListening()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopListening()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_PERMISSION_SETTING -> startListening()
        }
    }

    override fun onStateUpdated(state: TunerViewState) {
        state.apply {
            if(tuning.note != null) {
                val visibility = if(settings.basicMode) View.GONE else View.VISIBLE
                val tone = if(settings.flatSymbol && settings.solfegeNotation && tuning.note.semitone){
                    ChromaticScale.getSolfegeTone(ChromaticScale.getFlatTone(tuning.note.tone))
                } else if(settings.flatSymbol && tuning.note.semitone) {
                    ChromaticScale.getFlatTone(tuning.note.tone)
                } else if(settings.solfegeNotation) {
                    ChromaticScale.getSolfegeTone(tuning.note.tone)
                } else {
                    tuning.note.tone
                }
                val semitone = if(tuning.note.semitone) {
                    getString(if (settings.flatSymbol) cafe.adriel.chroma.R.string.flat_symbol else cafe.adriel.chroma.R.string.sharp_symbol)
                } else {
                    ""
                }

                if(vStandardFrequencyUnit.text.isNullOrEmpty()) vStandardFrequencyUnit.text = getString(cafe.adriel.chroma.R.string.hertz_unit)
                if(vFrequencyUnit.text.isNullOrEmpty()) vFrequencyUnit.text = getString(cafe.adriel.chroma.R.string.hertz_unit)
                if(vDeviationUnit.text.isNullOrEmpty()) vDeviationUnit.text = getString(cafe.adriel.chroma.R.string.deviation_unit)

                vMakeNoise.visibility = View.GONE
                vGivePermission.visibility = View.GONE
                vStandardFrequency.visibility = visibility
                vFrequency.visibility = visibility
                vOctave.visibility = visibility
                vDeviation.visibility = visibility
                vStandardFrequencyUnit.visibility = visibility
                vFrequencyUnit.visibility = visibility
                vDeviationUnit.visibility = visibility

                vStandardFrequency.text = "%.2f".format(tuning.note.frequency)
                vFrequency.text = "%.2f".format(tuning.frequency)
                vTone.text = tone
                vSemitone.text = semitone
                vOctave.text = tuning.note.octave.toString()
                vDeviation.text = tuning.deviation.toString()
                vDeviationBars.deviation = tuning.deviation
                vDeviationBars.precision = settings.precision

                vDeviation.setTextColor(color(getDeviationColorRes(tuning.deviation, settings.precision)))
                vDeviationUnit.setTextColor(color(getDeviationColorRes(tuning.deviation, settings.precision)))
            }
        }
    }

    private fun startListening(){
        launch {
            val snackBar = Snackbar.make(requireView(), getString(R.string.permission_needed), Snackbar.LENGTH_LONG)
            val snackBarRationale = SnackBarRationale(snackBar, getString(R.string.request_again))
            val result = Peko.requestPermissionsAsync(requireActivity(), Manifest.permission.RECORD_AUDIO, rationale = snackBarRationale)
            if (Manifest.permission.RECORD_AUDIO in result.grantedPermissions) {
                vDeviationBars.animateBars()
                viewModel.startListening()
            } else {
                vGivePermission.visibility = View.VISIBLE
                vGivePermission.setOnClickListener {
                    vGivePermission.visibility = View.GONE
                    showAppSettings()
                }
            }
        }
    }

    private fun showAppSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
    }

}