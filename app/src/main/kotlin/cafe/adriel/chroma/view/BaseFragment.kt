package cafe.adriel.chroma.view

import cafe.adriel.androidcoroutinescopes.appcompat.CoroutineScopedFragment
import com.etiennelenhart.eiffel.state.ViewState

abstract class BaseFragment<S: ViewState> : CoroutineScopedFragment() {

    abstract fun onStateUpdated(state: S)

}