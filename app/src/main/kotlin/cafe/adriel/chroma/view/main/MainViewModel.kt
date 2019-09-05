package cafe.adriel.chroma.view.main

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cafe.adriel.chroma.App
import cafe.adriel.chroma.BuildConfig
import com.crashlytics.android.Crashlytics
import com.etiennelenhart.eiffel.viewmodel.StateViewModel
import com.github.stephenvinouze.core.managers.KinAppManager
import com.github.stephenvinouze.core.models.KinAppProductType
import com.github.stephenvinouze.core.models.KinAppPurchase
import com.github.stephenvinouze.core.models.KinAppPurchaseResult
import kotlinx.coroutines.launch
import org.rewedigital.katana.Component
import org.rewedigital.katana.KatanaTrait
import org.rewedigital.katana.inject

class MainViewModel : StateViewModel<MainViewState>(), KatanaTrait, KinAppManager.KinAppListener {

    override val component = Component(dependsOn = listOf(App.appComponent))
    override val state = MutableLiveData<MainViewState>()

    private val billingManager by inject<KinAppManager>()

    init {
        billingManager.bind(this)
        initState { MainViewState() }
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.unbind()
    }

    override fun onBillingReady() {
        viewModelScope.launch {
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
        viewModelScope.launch {
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
