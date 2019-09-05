package cafe.adriel.chroma.view.main

import com.etiennelenhart.eiffel.state.ViewState

data class MainViewState(
    val event: MainViewEvent? = null
) : ViewState
