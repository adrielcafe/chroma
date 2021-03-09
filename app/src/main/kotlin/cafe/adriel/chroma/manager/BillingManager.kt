package cafe.adriel.chroma.manager

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cafe.adriel.chroma.BuildConfig
import com.github.stephenvinouze.core.managers.KinAppManager
import com.github.stephenvinouze.core.models.KinAppProductType
import com.github.stephenvinouze.core.models.KinAppPurchase
import com.github.stephenvinouze.core.models.KinAppPurchaseResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val kin: KinAppManager,
    private val scope: CoroutineScope
) : LifecycleEventObserver, KinAppManager.KinAppListener {

    sealed class Event {
        data class BillingSupported(val supported: Boolean) : Event()
        data class PurchaseCompleted(val success: Boolean) : Event()
    }

    private val eventFlow by lazy { MutableSharedFlow<Event>() }

    fun observe(owner: LifecycleOwner): Flow<Event> {
        owner.lifecycle.addObserver(this)
        return eventFlow.asSharedFlow()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> kin.bind(this)
            Lifecycle.Event.ON_STOP -> kin.unbind()
        }
    }

    override fun onBillingReady() {
        scope.launch {
            val supported = try {
                kin.restorePurchases(KinAppProductType.INAPP)?.forEach {
                    kin.consumePurchase(it)
                }
                kin.isBillingSupported(KinAppProductType.INAPP)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }

            eventFlow.emit(Event.BillingSupported(supported))
        }
    }

    override fun onPurchaseFinished(purchaseResult: KinAppPurchaseResult, purchase: KinAppPurchase?) {
        scope.launch {
            val success = if (purchaseResult == KinAppPurchaseResult.SUCCESS && purchase != null) {
                kin.consumePurchase(purchase).await()
            } else {
                false
            }

            eventFlow.emit(Event.PurchaseCompleted(success))
        }
    }

    fun verifyDonation(requestCode: Int, resultCode: Int, data: Intent?) =
        try {
            kin.verifyPurchase(requestCode, resultCode, data)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }

    fun donate(activity: Activity, sku: String) {
        if (BuildConfig.RELEASE) {
            if (sku.isNotBlank()) {
                kin.purchase(activity, sku, KinAppProductType.INAPP)
            }
        } else {
            kin.purchase(activity, KinAppManager.TEST_PURCHASE_SUCCESS, KinAppProductType.INAPP)
        }
    }
}
