package cafe.adriel.chroma.view

import android.os.Bundle
import cafe.adriel.androidcoroutinescopes.appcompat.CoroutineScopedActivity
import com.etiennelenhart.eiffel.state.ViewState
import com.etiennelenhart.eiffel.viewmodel.StateViewModel

abstract class BaseActivity<S: ViewState> : CoroutineScopedActivity() {

    protected abstract val viewModel: StateViewModel<S>

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.observeState(this, ::onStateUpdated)
    }

    protected abstract fun onStateUpdated(state: S)

}