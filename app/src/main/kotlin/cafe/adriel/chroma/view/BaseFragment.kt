package cafe.adriel.chroma.view

import android.os.Bundle
import android.view.View
import cafe.adriel.androidcoroutinescopes.appcompat.CoroutineScopedFragment
import com.etiennelenhart.eiffel.state.ViewState
import com.etiennelenhart.eiffel.viewmodel.StateViewModel

abstract class BaseFragment<S: ViewState> : CoroutineScopedFragment() {

    protected abstract val viewModel: StateViewModel<S>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState(this, ::onStateUpdated)
    }

    protected abstract fun onStateUpdated(state: S)

}