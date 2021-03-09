package cafe.adriel.chroma.di

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.chroma.manager.BillingManager
import cafe.adriel.chroma.manager.PermissionManager
import cafe.adriel.chroma.manager.SettingsManager
import cafe.adriel.chroma.manager.TunerManager
import cafe.adriel.chroma.view.compose.TunerActivity
import cafe.adriel.chroma.view.compose.TunerScreen
import cafe.adriel.chroma.view.compose.TunerViewModel
import cafe.adriel.satchel.Satchel
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import com.github.stephenvinouze.core.managers.KinAppManager
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {

    scope<TunerActivity> {
        viewModel {
            TunerViewModel(
                tunerManager = get(),
                settingsManager = get(),
                permissionManager = get()
            )
        }

        scoped {
            TunerScreen(
                viewModel = get(),
                permissionManager = get()
            )
        }

        scoped {
            BillingManager(
                kin = get(),
                scope = getSource<TunerActivity>().lifecycleScope
            )
        }

        scoped {
            TunerManager(
                settingsManager = get(),
                permissionManager = get(),
                lifecycleOwner = getSource<TunerActivity>()
            )
        }

        scoped {
            PermissionManager(
                activity = getSource<TunerActivity>()
            )
        }
    }

    single {
        SettingsManager(
            storage = get(),
            scope = get()
        )
    }

    single {
        val file = File(get<Application>().filesDir, "settings.storage")
        Satchel.with(
            storer = FileSatchelStorer(file)
        )
    }

    single {
        KinAppManager(
            context = get<Application>(),
            developerPayload = ""
        )
    }

    single<CoroutineScope> {
        ProcessLifecycleOwner.get().lifecycleScope
    }
}
