package cafe.adriel.chroma.util

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.etiennelenhart.eiffel.state.ViewState
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

inline fun <reified T : ViewModel> getViewModel(app: Application) =
    StateAndroidViewModelFactory.getInstance(app).create(T::class.java)

open class StateAndroidViewModel<T: ViewState>(protected val app: Application) : StateViewModel<T>(), CoroutineScope {

    override val state = MutableLiveData<T>()

    private val coroutineJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

}

class StateAndroidViewModelFactory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        private var sInstance: StateAndroidViewModelFactory? = null

        fun getInstance(app: Application): StateAndroidViewModelFactory {
            if (sInstance == null) sInstance = StateAndroidViewModelFactory(app)
            return sInstance as StateAndroidViewModelFactory
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(app)
    }

}