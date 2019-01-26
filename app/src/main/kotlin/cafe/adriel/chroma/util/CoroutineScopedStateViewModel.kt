package cafe.adriel.chroma.util

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.etiennelenhart.eiffel.state.ViewState
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class CoroutineScopedStateViewModel<T: ViewState>(val app: Application) : StateViewModel<T>(), CoroutineScope {

    override val state = MutableLiveData<T>()

    protected val coroutineJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

}