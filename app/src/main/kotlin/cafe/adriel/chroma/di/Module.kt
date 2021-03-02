package cafe.adriel.chroma.di

import android.app.Application
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import cafe.adriel.chroma.manager.BillingManager
import cafe.adriel.chroma.manager.TunerManager
import cafe.adriel.chroma.view.main.MainActivity
import cafe.adriel.chroma.view.main.tuner.TunerViewModel
import com.github.stephenvinouze.core.managers.KinAppManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    scope<MainActivity> {
        scoped { BillingManager(kin = get(), scope = getSource<MainActivity>().lifecycleScope) }
    }

    viewModel { TunerViewModel(preferences = get(), tunerManager = get()) }

    factory { TunerManager() }

    factory { PreferenceManager.getDefaultSharedPreferences(get<Application>()) }

    factory { KinAppManager(get<Application>(), developerPayload = "") }
}
