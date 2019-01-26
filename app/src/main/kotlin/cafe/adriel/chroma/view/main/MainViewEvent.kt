package cafe.adriel.chroma.view.main

import com.etiennelenhart.eiffel.state.ViewEvent

sealed class MainViewEvent : ViewEvent() {
    class BillingSupportedEvent(val supported: Boolean) : MainViewEvent()
    class PurchaseCompletedEvent(val success: Boolean) : MainViewEvent()
}