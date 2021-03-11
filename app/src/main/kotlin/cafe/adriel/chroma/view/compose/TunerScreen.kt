package cafe.adriel.chroma.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.chroma.R
import cafe.adriel.chroma.manager.PermissionManager
import cafe.adriel.chroma.model.Settings
import cafe.adriel.chroma.model.Tuning
import cafe.adriel.chroma.model.TuningDeviationResult
import cafe.adriel.chroma.view.compose.components.RequestPermissionSnackbar
import cafe.adriel.chroma.view.compose.components.TuningDeviationBars
import cafe.adriel.chroma.view.compose.components.TuningInfo
import cafe.adriel.chroma.view.compose.components.TuningNote
import cafe.adriel.chroma.view.compose.theme.ChromaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TunerScreen(
    private val viewModel: TunerViewModel,
    private val permissionManager: PermissionManager
) {

    @Composable
    fun Content() {
        val screenState by viewModel.state.collectAsState()
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        ChromaTheme {
            Scaffold(
                topBar = {
                    TunerTopBar(scope, scaffoldState.drawerState)
                },
                content = {
                    TunerContent(screenState.tuning, screenState.settings)

                    if (screenState.hasRequiredPermissions.not()) {
                        RequestPermissionSnackbar(scaffoldState.snackbarHostState) {
                            permissionManager.showExternalAppSettings()
                        }
                    }
                },
                drawerContent = {
                    TunerDrawer()
                },
                drawerScrimColor = Color.Black.copy(alpha = DrawerDefaults.ScrimOpacity),
                drawerGesturesEnabled = false,
                scaffoldState = scaffoldState
            )
        }
    }

    @Composable
    private fun TunerTopBar(scope: CoroutineScope, drawerState: DrawerState) =
        TopAppBar(
            title = {
                Image(
                    painter = painterResource(R.drawable.img_logo),
                    contentDescription = "Chroma logo",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                    modifier = Modifier.size(width = 100.dp, height = 48.dp)
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            },
            backgroundColor = Color.Transparent,
            elevation = Dp.Hairline
        )

    @Composable
    private fun TunerContent(tuning: Tuning, settings: Settings) =
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (noteRef, deviationBarsRef, infoRef) = createRefs()
            val verticalGuideline = createGuidelineFromTop(.55f)

            if (tuning.note != null && tuning.deviationResult is TuningDeviationResult.Detected) {
                TuningNote(
                    note = tuning.note,
                    tone = tuning.getTone(settings),
                    semitone = tuning.getSemitoneSymbolRes(settings)?.let { stringResource(it) }.orEmpty(),
                    basicMode = settings.basicMode,
                    modifier = Modifier.constrainAs(noteRef) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(deviationBarsRef.top)
                    }
                )
            }

            TuningDeviationBars(
                deviationResult = tuning.deviationResult,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 24.dp)
                    .constrainAs(deviationBarsRef) {
                        centerAround(verticalGuideline)
                    }
            )

            if (tuning.deviationResult is TuningDeviationResult.Detected &&
                tuning.note != null &&
                settings.basicMode.not()
            ) {
                TuningInfo(
                    deviation = tuning.deviationResult.value,
                    frequency = tuning.formattedFrequency,
                    color = tuning.deviationResult.precision.color,
                    modifier = Modifier.constrainAs(infoRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(deviationBarsRef.bottom)
                    }
                )
            }
        }

    @Composable
    private fun TunerDrawer() =
        Unit
}
