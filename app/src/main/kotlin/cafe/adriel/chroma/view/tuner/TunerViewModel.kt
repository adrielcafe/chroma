package cafe.adriel.chroma.view.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.chroma.manager.BillingManager
import cafe.adriel.chroma.manager.MessagingManager
import cafe.adriel.chroma.manager.PermissionManager
import cafe.adriel.chroma.manager.SettingsManager
import cafe.adriel.chroma.manager.TunerManager
import cafe.adriel.chroma.model.DonationProduct
import cafe.adriel.chroma.model.settings.Settings
import cafe.adriel.chroma.model.tuner.Tuning
import cafe.adriel.chroma.model.tuner.TuningDeviationPrecision
import cafe.adriel.chroma.model.tuner.TuningDeviationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TunerViewModel(
    private val tunerManager: TunerManager,
    private val settingsManager: SettingsManager,
    private val permissionManager: PermissionManager,
    private val billingManager: BillingManager,
    private val messagingManager: MessagingManager
) : ViewModel() {

    private val _state by lazy {
        MutableStateFlow(
            TunerState(
                settings = settingsManager.settings,
                hasRequiredPermissions = permissionManager.hasRequiredPermissions
            )
        )
    }
    val state by lazy { _state.asStateFlow() }

    init {
        viewModelScope.launch {
            requestPermissions()
            playStartAnimation()
            setupState()
        }
    }

    fun updateSettings(settings: Settings) {
        settingsManager.settings = settings
    }

    fun donate(product: DonationProduct) {
        billingManager.donate(product)
    }

    private fun setupState() {
        tunerManager.state
            .mergeState { state, tuning ->
                state.copy(tuning = tuning)
            }
            .launchIn(viewModelScope)

        settingsManager.state
            .mergeState { state, settings ->
                tunerManager.restartListener()
                state.copy(settings = settings)
            }
            .launchIn(viewModelScope)

        permissionManager.state
            .mergeState { state, hasRequiredPermissions ->
                tunerManager.restartListener()
                state.copy(hasRequiredPermissions = hasRequiredPermissions)
            }
            .launchIn(viewModelScope)

        billingManager.state
            .mergeState { state, isBillingSupported ->
                state.copy(isBillingSupported = isBillingSupported)
            }
            .launchIn(viewModelScope)

        messagingManager.state
            .mergeState { state, message ->
                state.copy(message = message)
            }
            .launchIn(viewModelScope)
    }

    fun consumeMessage() {
        messagingManager.consume()
    }

    private suspend fun requestPermissions() {
        if (permissionManager.hasRequiredPermissions.not()) {
            permissionManager.requestPermissions()
            _state.value = _state.value.copy(hasRequiredPermissions = permissionManager.hasRequiredPermissions)
        }
    }

    private suspend fun playStartAnimation() {
        delay(500)

        (50 downTo 0 step 10).forEach { deviation ->
            updateAnimation(deviation)
            delay(150)
        }

        (0..50 step 10).forEach { deviation ->
            updateAnimation(deviation)
            delay(150)
        }

        _state.value = _state.value.copy(
            tuning = Tuning(
                deviation = TuningDeviationResult.NotDetected
            )
        )

        delay(500)
    }

    private fun updateAnimation(deviation: Int) {
        _state.value = _state.value.copy(
            tuning = Tuning(
                deviation = TuningDeviationResult.Animation(
                    negativeValue = -deviation,
                    negativePrecision = TuningDeviationPrecision.fromDeviation(
                        deviation = -deviation,
                        offset = settingsManager.tunerDeviationPrecision.offset
                    ),
                    positiveValue = deviation,
                    positivePrecision = TuningDeviationPrecision.fromDeviation(
                        deviation = deviation,
                        offset = settingsManager.tunerDeviationPrecision.offset
                    )
                )
            )
        )
    }

    private fun <T> Flow<T>.mergeState(action: (TunerState, T) -> TunerState): Flow<T> =
        onEach { value ->
            _state.value = action(_state.value, value)
        }
}
