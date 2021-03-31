package cafe.adriel.chroma.ktx

import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterialApi::class)
suspend fun BackdropScaffoldState.toggle() {
    if (isConcealed) reveal()
    else conceal()
}
