package cafe.adriel.chroma.view.main

import android.app.Activity
import android.app.Application
import android.content.Intent
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.util.StateAndroidViewModel
import com.crashlytics.android.Crashlytics
import com.github.stephenvinouze.core.managers.KinAppManager
import com.github.stephenvinouze.core.models.KinAppProductType
import com.github.stephenvinouze.core.models.KinAppPurchase
import com.github.stephenvinouze.core.models.KinAppPurchaseResult
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : StateAndroidViewModel<MainViewState>(app), KinAppManager.KinAppListener {

    private val billingManager by lazy {
        KinAppManager(app, "")
    }

    init {
        billingManager.bind(this)
        initState { MainViewState() }
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.unbind()
    }

    override fun onBillingReady() {
        launch {
            val billingSupported = try {
                billingManager.restorePurchases(KinAppProductType.INAPP)?.forEach {
                    billingManager.consumePurchase(it)
                }
                billingManager.isBillingSupported(KinAppProductType.INAPP)
            } catch (e: Exception) {
                Crashlytics.logException(e)
                e.printStackTrace()
                false
            }
            updateState {
                it.copy(event = MainViewEvent.BillingSupportedEvent(billingSupported))
            }
        }
    }

    override fun onPurchaseFinished(purchaseResult: KinAppPurchaseResult, purchase: KinAppPurchase?) {
        launch {
            val purchaseCompleted = if (purchaseResult == KinAppPurchaseResult.SUCCESS && purchase != null) {
                billingManager.consumePurchase(purchase).await()
            } else {
                false
            }
            updateState {
                it.copy(event = MainViewEvent.PurchaseCompletedEvent(purchaseCompleted))
            }
        }
    }

    fun verifyDonation(requestCode: Int, resultCode: Int, data: Intent?) =
        try {
            billingManager.verifyPurchase(requestCode, resultCode, data)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            e.printStackTrace()
            false
        }

    fun donate(activity: Activity, sku: String) {
        if (BuildConfig.RELEASE) {
            if (sku.isNotBlank()) {
                billingManager.purchase(activity, sku, KinAppProductType.INAPP)
            }
        } else {
            billingManager.purchase(
                activity,
                KinAppManager.TEST_PURCHASE_SUCCESS,
                KinAppProductType.INAPP
            )
        }
    }

}