package cafe.adriel.chroma.di

import androidx.preference.PreferenceManager
import cafe.adriel.chroma.view.main.MainViewModel
import cafe.adriel.chroma.view.main.tuner.TunerManager
import cafe.adriel.chroma.view.main.tuner.TunerViewModel
import com.github.stephenvinouze.core.managers.KinAppManager
import org.rewedigital.katana.Module
import org.rewedigital.katana.android.modules.APPLICATION_CONTEXT
import org.rewedigital.katana.androidx.viewmodel.viewModel
import org.rewedigital.katana.dsl.compact.factory
import org.rewedigital.katana.dsl.get

val appModule = Module {

    viewModel { MainViewModel() }
    viewModel { TunerViewModel(preferences = get(), tunerManager = get()) }

    factory { PreferenceManager.getDefaultSharedPreferences(get(APPLICATION_CONTEXT)) }
    factory { KinAppManager(get(APPLICATION_CONTEXT), developerPayload = "") }
    factory { TunerManager() }
}
