package cafe.adriel.chroma.view

import cafe.adriel.androidcoroutinescopes.appcompat.CoroutineScopedActivity
import com.etiennelenhart.eiffel.state.ViewState

abstract class BaseActivity<S: ViewState> : CoroutineScopedActivity() {

    abstract fun onStateUpdated(state: S)

}