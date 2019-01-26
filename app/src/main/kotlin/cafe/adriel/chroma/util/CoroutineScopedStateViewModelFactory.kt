package cafe.adriel.chroma.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class CoroutineScopedStateViewModelFactory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(app)
    }

    companion object {

        private var sInstance: CoroutineScopedStateViewModelFactory? = null

        fun getInstance(application: Application): CoroutineScopedStateViewModelFactory {
            if (sInstance == null) {
                sInstance = CoroutineScopedStateViewModelFactory(application)
            }
            return sInstance as CoroutineScopedStateViewModelFactory
        }
    }
}