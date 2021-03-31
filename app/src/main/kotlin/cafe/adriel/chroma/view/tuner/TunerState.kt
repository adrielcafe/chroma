package cafe.adriel.chroma.view.tuner

import cafe.adriel.chroma.model.settings.Settings
import cafe.adriel.chroma.model.tuner.Tuning

data class TunerState(
    val settings: Settings,
    val tuning: Tuning = Tuning(),
    val message: String? = null,
    val hasRequiredPermissions: Boolean = false,
    val isBillingSupported: Boolean = false
)
