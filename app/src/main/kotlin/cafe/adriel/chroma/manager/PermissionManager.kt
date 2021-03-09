package cafe.adriel.chroma.manager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.chroma.util.hasPermission
import com.markodevcic.peko.Peko
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionManager(
    private val activity: AppCompatActivity
) : LifecycleEventObserver {

    private val _state by lazy { MutableStateFlow(hasRequiredPermissions) }
    val state by lazy { _state.asStateFlow() }

    val hasRequiredPermissions: Boolean
        get() = activity.hasPermission(Manifest.permission.RECORD_AUDIO)

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> updateState()
        }
    }

    suspend fun requestPermission() {
        runCatching {
            Peko.requestPermissionsAsync(activity, Manifest.permission.RECORD_AUDIO)
            updateState()
        }
    }

    fun showExternalAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", activity.packageName, null))
            .let(activity::startActivity)
    }

    private fun updateState() {
        _state.value = hasRequiredPermissions
    }
}
