package cafe.adriel.chroma.view.main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import cafe.adriel.chroma.App
import cafe.adriel.chroma.BuildConfig
import cafe.adriel.chroma.R
import cafe.adriel.chroma.util.getViewModel
import cafe.adriel.chroma.util.open
import cafe.adriel.chroma.util.share
import cafe.adriel.chroma.view.BaseActivity
import cafe.adriel.chroma.view.main.settings.SettingsFragment
import cafe.adriel.chroma.view.main.tuner.TunerFragment
import cafe.adriel.nomanswallpaper.view.main.dialog.AboutDialog
import cafe.adriel.nomanswallpaper.view.main.dialog.DonateDialog
import com.etiennelenhart.eiffel.state.peek
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivity : BaseActivity<MainViewState>(), NavigationView.OnNavigationItemSelectedListener, DonateDialog.OnDonateListener {

    companion object {
        private const val SECTION_INDEX_TUNER = 0
        private const val SECTION_INDEX_SETTINGS = 1
    }

    override val viewModel by lazy { getViewModel<MainViewModel>(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(vToolbar)

        vDrawerNav.menu.getItem(0).isChecked = true
        vDrawerNav.menu.performIdentifierAction(R.id.nav_tuner, 0)
        vDrawerNav.getHeaderView(0).vAppVersion.text = BuildConfig.VERSION_NAME

        val drawerToggle = ActionBarDrawerToggle(this, vDrawer, vToolbar, R.string.open_menu, R.string.close_menu)
        vDrawerNav.setNavigationItemSelectedListener(this)
        vDrawer.addDrawerListener(drawerToggle)
        drawerToggle.drawerArrowDrawable.color = Color.WHITE
        drawerToggle.syncState()

        val adapter = SectionsPagerAdapter(supportFragmentManager)
        vContent.adapter = adapter
        vContent.offscreenPageLimit = adapter.count
    }

    override fun onBackPressed() {
        if (vDrawer.isDrawerOpen(GravityCompat.START)) {
            vDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        when(vContent?.currentItem){
            0 -> goToTuner()
            1 -> goToSettings()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!viewModel.verifyDonation(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        vDrawer.postDelayed({
            vDrawer.closeDrawer(GravityCompat.START)
        }, 100)
        when (item.itemId) {
            R.id.nav_tuner -> goToTuner()
            R.id.nav_settings -> goToSettings()
            R.id.nav_about -> AboutDialog.show(this)
            R.id.nav_donate -> DonateDialog.show(this)
            R.id.nav_share -> shareApp()
            R.id.nav_rate -> rateApp()
        }
        return true
    }

    override fun onDonate(sku: String) {
        viewModel.donate(this, sku)
    }

    override fun onStateUpdated(state: MainViewState) {
        state.event?.peek {
            when(it){
                is MainViewEvent.BillingSupportedEvent -> {
                    vDrawerNav.menu
                        .findItem(R.id.nav_donate)
                        .isVisible = it.supported
                    true
                }
                is MainViewEvent.PurchaseCompletedEvent -> {
                    if (it.success) Snackbar.make(vRoot, R.string.thanks_for_support, Snackbar.LENGTH_LONG).show()
                    true
                }
            }
        }
    }

    private fun goToTuner() {
        vToolbar.title = getString(R.string.tuner)
        vContent.currentItem = SECTION_INDEX_TUNER
        vDrawerNav.menu.getItem(SECTION_INDEX_TUNER).isChecked = true
    }

    private fun goToSettings() {
        vToolbar.title = getString(R.string.settings)
        vContent.currentItem = SECTION_INDEX_SETTINGS
        vDrawerNav.menu.getItem(SECTION_INDEX_SETTINGS).isChecked = true
    }

    private fun shareApp() {
        "${getString(R.string.you_should_try)}\n${App.PLAY_STORE_URL}".share(this)
    }

    private fun rateApp() {
        showAppInPlayStore()
    }

    private fun showAppInPlayStore() {
        try {
            Uri.parse(App.MARKET_URL).open(this)
        } catch (e: Exception) {
            Uri.parse(App.PLAY_STORE_URL).open(this)
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val sections by lazy { listOf(TunerFragment(), SettingsFragment()) }

        override fun getItem(position: Int) = sections[position]

        override fun getCount() = sections.size

    }

}