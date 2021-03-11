package cafe.adriel.chroma.view

import cafe.adriel.chroma.model.settings.Settings
import cafe.adriel.chroma.model.tuner.Tuning

data class TunerState(
    val tuning: Tuning,
    val settings: Settings,
    val hasRequiredPermissions: Boolean
)
