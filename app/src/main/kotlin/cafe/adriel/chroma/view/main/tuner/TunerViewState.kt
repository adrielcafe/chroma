package cafe.adriel.chroma.view.main.tuner

import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import com.etiennelenhart.eiffel.state.ViewState

data class TunerViewState(val tuning: Tuning,
                          val settings: Settings) : ViewState