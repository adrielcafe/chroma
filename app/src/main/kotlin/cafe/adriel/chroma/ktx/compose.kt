package cafe.adriel.chroma.ktx

import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
fun BackdropScaffoldState.toggle(scope: CoroutineScope) {
    scope.launch {
        if (isConcealed) reveal()
        else conceal()
    }
}
