package cafe.adriel.chroma.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.chroma.App
import com.etiennelenhart.eiffel.state.ViewState
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import org.rewedigital.katana.Component
import org.rewedigital.katana.KatanaTrait

abstract class BaseActivity<S : ViewState> : AppCompatActivity(), KatanaTrait {

    override val component = Component(dependsOn = listOf(App.appComponent))

    protected abstract val viewModel: StateViewModel<S>

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.observeState(this, ::onStateUpdated)
    }

    protected abstract fun onStateUpdated(state: S)
}
