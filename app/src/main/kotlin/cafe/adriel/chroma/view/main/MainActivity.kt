package cafe.adriel.chroma.view.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import cafe.adriel.chroma.R
import cafe.adriel.chroma.manager.BillingManager
import cafe.adriel.chroma.util.tag
import cafe.adriel.chroma.view.main.dialog.DonateDialog
import cafe.adriel.chroma.view.main.settings.SettingsFragment
import cafe.adriel.chroma.view.main.tuner.TunerFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.activityScope

class MainActivity : AppCompatActivity(), DonateDialog.OnDonateListener {

    private val billingManager by activityScope().inject<BillingManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(vToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportFragmentManager.commit {
            replace(R.id.vContent, TunerFragment(), tag<TunerFragment>())
            replace(R.id.vSettingsNav, SettingsFragment(), tag<SettingsFragment>())
        }

        billingManager
            .observe(this)
            .onEach(::handleBillingEvent)
            .launchIn(lifecycleScope)
    }

    override fun onBackPressed() {
        if (vDrawer.isDrawerOpen(GravityCompat.END)) {
            vDrawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!billingManager.verifyDonation(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.forEach {
            MenuItemCompat.setIconTintList(it, ColorStateList.valueOf(Color.WHITE))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> {
            vDrawer.openDrawer(GravityCompat.END)
            true
        }
        else -> false
    }

    private fun handleBillingEvent(event: BillingManager.Event) {
        when (event) {
            is BillingManager.Event.BillingSupported -> {
                val frag = supportFragmentManager.findFragmentByTag(tag<SettingsFragment>())
                if (frag is SettingsFragment) {
                    frag.setBillingSupported(event.supported)
                }
            }
            is BillingManager.Event.PurchaseCompleted -> {
                if (event.success) Snackbar.make(vRoot, R.string.thanks_for_support, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDonate(sku: String) {
        vDrawer.closeDrawer(GravityCompat.END)
        billingManager.donate(this, sku)
    }
}
