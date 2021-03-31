package cafe.adriel.chroma.view.components

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
        when (state.showSnackbar(message, action, SnackbarDuration.Indefinite)) {
            SnackbarResult.ActionPerformed -> onActionClicked()
        }
    }
}

@Composable
fun MessageSnackbar(message: String, state: SnackbarHostState, onDismissed: () -> Unit) {
    LaunchedEffect(state) {
        when (state.showSnackbar(message, duration = SnackbarDuration.Short)) {
            SnackbarResult.Dismissed -> onDismissed()
        }
    }
}
