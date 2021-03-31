package cafe.adriel.chroma.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.R
import cafe.adriel.chroma.model.settings.DonationProduct
import com.github.stephenvinouze.core.managers.KinAppManager
import com.github.stephenvinouze.core.models.KinAppProductType
import com.github.stephenvinouze.core.models.KinAppPurchase
import com.github.stephenvinouze.core.models.KinAppPurchaseResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val activity: AppCompatActivity,
    private val messagingManager: MessagingManager,
    private val kin: KinAppManager,
    private val scope: CoroutineScope
) : LifecycleEventObserver, KinAppManager.KinAppListener {

    private val _state by lazy { MutableStateFlow(false) }
    val state by lazy { _state.asStateFlow() }

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> kin.bind(this)
            Lifecycle.Event.ON_STOP -> kin.unbind()
        }
    }

    override fun onBillingReady() {
        scope.launch {
            _state.value = try {
                kin.restorePurchases(KinAppProductType.INAPP)?.forEach {
                    kin.consumePurchase(it)
                }
                kin.isBillingSupported(KinAppProductType.INAPP)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
    }

    override fun onPurchaseFinished(purchaseResult: KinAppPurchaseResult, purchase: KinAppPurchase?) {
        scope.launch {
            val success = if (purchaseResult == KinAppPurchaseResult.SUCCESS && purchase != null) {
                kin.consumePurchase(purchase).await()
            } else {
                false
            }

            if (success) {
                messagingManager.send(R.string.thanks_for_support)
            }
        }
    }

    fun verifyPurchase(requestCode: Int, resultCode: Int, data: Intent?) =
        try {
            kin.verifyPurchase(requestCode, resultCode, data)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }

    fun donate(product: DonationProduct) {
        if (BuildConfig.RELEASE) {
            kin.purchase(activity, product.sku, KinAppProductType.INAPP)
        } else {
            kin.purchase(activity, KinAppManager.TEST_PURCHASE_SUCCESS, KinAppProductType.INAPP)
        }
    }
}
