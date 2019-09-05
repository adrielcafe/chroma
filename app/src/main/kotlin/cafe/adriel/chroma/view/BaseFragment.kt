package cafe.adriel.chroma.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import cafe.adriel.chroma.App
import com.etiennelenhart.eiffel.state.ViewState
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import org.rewedigital.katana.Component
import org.rewedigital.katana.KatanaTrait

abstract class BaseFragment<S : ViewState> : Fragment(), KatanaTrait {

    override val component = Component(dependsOn = listOf(App.appComponent))

    protected abstract val viewModel: StateViewModel<S>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState(this, ::onStateUpdated)
    }

    protected abstract fun onStateUpdated(state: S)
}
