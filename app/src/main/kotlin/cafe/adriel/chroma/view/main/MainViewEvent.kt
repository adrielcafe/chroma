package cafe.adriel.chroma.view.main

import com.etiennelenhart.eiffel.state.ViewEvent

sealed class MainViewEvent : ViewEvent() {
    data class BillingSupportedEvent(val supported: Boolean) : MainViewEvent()
    data class PurchaseCompletedEvent(val success: Boolean) : MainViewEvent()
}
