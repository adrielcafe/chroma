package cafe.adriel.chroma.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.R
import cafe.adriel.chroma.ktx.logError
import cafe.adriel.chroma.model.DonationProduct
import com.github.stephenvinouze.core.managers.KinAppManager
import com.github.stephenvinouze.core.models.KinAppProductType
import com.github.stephenvinouze.core.models.KinAppPurchase
import com.github.stephenvinouze.core.models.KinAppPurchaseResult
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
        _state.value = runCatching {
            kin.restorePurchases(KinAppProductType.INAPP)?.consumeAll()
            kin.isBillingSupported(KinAppProductType.INAPP)
        }
            .onFailure(::logError)
            .getOrDefault(false)
    }

    override fun onPurchaseFinished(purchaseResult: KinAppPurchaseResult, purchase: KinAppPurchase?) {
        scope.launch {
            runCatching {
                if (purchaseResult == KinAppPurchaseResult.SUCCESS && purchase != null) {
                    kin.consumePurchase(purchase).await()
                } else {
                    false
                }
            }.fold(
                onSuccess = { consumed ->
                    if (consumed) {
                        messagingManager.send(R.string.thanks_for_support)
                    }
                },
                onFailure = ::logError
            )
        }
    }

    fun verifyPurchase(requestCode: Int, resultCode: Int, data: Intent?) =
        runCatching { kin.verifyPurchase(requestCode, resultCode, data) }
            .onFailure(::logError)
            .getOrDefault(false)

    fun donate(product: DonationProduct) {
        runCatching {
            val sku = if (BuildConfig.RELEASE) product.sku else KinAppManager.TEST_PURCHASE_SUCCESS
            kin.purchase(activity, sku, KinAppProductType.INAPP)
        }.onFailure(::logError)
    }

    private fun List<KinAppPurchase>.consumeAll() {
        scope.launch {
            forEach { kin.consumePurchase(it) }
        }
    }
}
