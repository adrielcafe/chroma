package cafe.adriel.chroma.view.main.tuner

import com.etiennelenhart.eiffel.state.ViewEvent

sealed class TunerViewEvent : ViewEvent() {
    object SettingsChangedEvent : TunerViewEvent()
}
