package cafe.adriel.chroma.view.compose.components

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import cafe.adriel.chroma.R

@Composable
fun RequestPermissionSnackbar(state: SnackbarHostState, onActionClicked: () -> Unit) {
    val message = stringResource(R.string.permission_needed)
    val action = stringResource(R.string.give_permission)

    LaunchedEffect(state) {
        val result = state.showSnackbar(message, action, SnackbarDuration.Indefinite)
        if (result == SnackbarResult.ActionPerformed) {
            onActionClicked()
        }
    }
}
