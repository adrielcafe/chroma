package cafe.adriel.chroma.view.compose

import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning

data class TunerState(
    val tuning: Tuning,
    val settings: Settings,
    val hasRequiredPermissions: Boolean
)
