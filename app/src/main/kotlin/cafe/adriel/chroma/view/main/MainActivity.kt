package cafe.adriel.chroma.view.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.fragment.app.commit
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.tag
import cafe.adriel.chroma.view.BaseActivity
import cafe.adriel.chroma.view.main.dialog.DonateDialog
import cafe.adriel.chroma.view.main.settings.SettingsFragment
import cafe.adriel.chroma.view.main.tuner.TunerFragment
import com.etiennelenhart.eiffel.state.peek
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.rewedigital.katana.androidx.viewmodel.viewModel

class MainActivity : BaseActivity<MainViewState>(), DonateDialog.OnDonateListener {

    override val viewModel by viewModel<MainViewModel, MainActivity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(vToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportFragmentManager.commit {
            replace(R.id.vContent, TunerFragment(), tag<TunerFragment>())
            replace(R.id.vSettingsNav, SettingsFragment(), tag<SettingsFragment>())
        }
    }

    override fun onBackPressed() {
        if (vDrawer.isDrawerOpen(GravityCompat.END)) {
            vDrawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!viewModel.verifyDonation(requestCode, resultCode, data)) {
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

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.settings -> {
            vDrawer.openDrawer(GravityCompat.END)
            true
        }
        else -> false
    }

    override fun onStateUpdated(state: MainViewState) {
        state.event?.peek {
            when (it) {
                is MainViewEvent.BillingSupportedEvent -> {
                    val frag = supportFragmentManager.findFragmentByTag(tag<SettingsFragment>())
                    if (frag is SettingsFragment) {
                        frag.setBillingSupported(it.supported)
                    }
                    true
                }
                is MainViewEvent.PurchaseCompletedEvent -> {
                    if (it.success) Snackbar.make(vRoot, R.string.thanks_for_support, Snackbar.LENGTH_LONG).show()
                    true
                }
            }
        }
    }

    override fun onDonate(sku: String) {
        vDrawer.closeDrawer(GravityCompat.END)
        viewModel.donate(this, sku)
    }
}
