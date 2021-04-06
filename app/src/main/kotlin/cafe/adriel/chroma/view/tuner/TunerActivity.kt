package cafe.adriel.chroma.view.tuner

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import cafe.adriel.chroma.ktx.keepScreenOn
import cafe.adriel.chroma.manager.BillingManager
import org.koin.androidx.scope.ScopeActivity

class TunerActivity : ScopeActivity() {

    private val screen by inject<TunerScreen>()
    private val billingManager by inject<BillingManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { screen.Content() }
        keepScreenOn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (billingManager.verifyPurchase(requestCode, resultCode, data).not()) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
