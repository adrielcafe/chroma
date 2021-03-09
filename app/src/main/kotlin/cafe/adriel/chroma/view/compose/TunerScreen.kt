package cafe.adriel.chroma.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastMap
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.chroma.R
import cafe.adriel.chroma.manager.PermissionManager
import cafe.adriel.chroma.model.*
import cafe.adriel.chroma.view.compose.theme.ChromaTheme
import cafe.adriel.chroma.view.compose.theme.ChromaTypography
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
                        RequestPermissionSnackbar(scaffoldState.snackbarHostState)
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
                Text(
                    text = stringResource(R.string.app_name),
                    color = MaterialTheme.colors.secondary,
                    fontFamily = ChromaTypography.pacificoFontFamily,
                    style = MaterialTheme.typography.h4
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
            if (tuning.note != null && tuning.deviationResult is TuningDeviationResult.Detected && settings.basicMode.not()) {
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

    @Composable
    private fun TuningNote(note: ChromaticScale, tone: String, semitone: String, basicMode: Boolean, modifier: Modifier = Modifier) {
        ConstraintLayout(modifier = modifier) {
            val (toneRef, semitoneRef, octaveRef, frequencyRef) = createRefs()

            Text(
                text = tone,
                color = MaterialTheme.colors.onBackground,
                fontSize = 150.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(toneRef) {
                    centerVerticallyTo(parent)
                }
            )
            Text(
                text = semitone,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h2,
                modifier = Modifier.constrainAs(semitoneRef) {
                    start.linkTo(toneRef.end, margin = 12.dp)
                    top.linkTo(toneRef.top)
                }
            )
            if (basicMode.not()) {
                Text(
                    text = note.octave.toString(),
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.constrainAs(octaveRef) {
                        start.linkTo(toneRef.end, margin = 12.dp)
                        bottom.linkTo(toneRef.bottom, margin = 20.dp)
                    }
                )
                TuningValue(
                    value = note.formattedFrequency,
                    unit = TuningUnit.HERTZ,
                    color = MaterialTheme.colors.onBackground.copy(alpha = .5f),
                    valueStyle = MaterialTheme.typography.h5,
                    unitStyle = MaterialTheme.typography.h6,
                    modifier = Modifier.constrainAs(frequencyRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(octaveRef.bottom, margin = (-16).dp)
                    }
                )
            }
        }
    }

    @Composable
    private fun TuningValue(value: String, unit: String, valueStyle: TextStyle, unitStyle: TextStyle, color: Color, modifier: Modifier = Modifier) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = modifier
        ) {
           Text(
               text = value,
               color = color,
               style = valueStyle
           )
           Text(
               text = unit,
               color = color,
               style = unitStyle.copy(baselineShift = BaselineShift.Subscript),
               modifier = Modifier.padding(start = 4.dp)
           )
        }
    }

    @Composable
    private fun TuningDeviationBars(deviationResult: TuningDeviationResult, modifier: Modifier = Modifier) =
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
           TuningDeviationPrecision.values().forEach { item ->
               TuningDeviationBar(
                   color = item.color,
                   height = item.barHeight,
                   active = when (deviationResult) {
                       is TuningDeviationResult.NotDetected -> false
                       is TuningDeviationResult.Detected -> item == deviationResult.precision
                       is TuningDeviationResult.Animation -> item in setOf(deviationResult.negativePrecision, deviationResult.positivePrecision)
                   }
               )
           }
        }

    @Composable
    private fun TuningDeviationBar(color: Color, height: Dp, active: Boolean) =
        Spacer(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(width = 3.dp, height = height)
                .background(
                    if (active) color
                    else MaterialTheme.colors.onBackground.copy(alpha = .2f)
                )
        )

    @Composable
    fun TuningInfo(deviation: Int, frequency: String, color: Color, modifier: Modifier = Modifier) =
        Column(modifier = modifier) {
            TuningValue(
                value = deviation.toString(),
                unit = TuningUnit.CENTS,
                color = color,
                valueStyle = MaterialTheme.typography.h3,
                unitStyle = MaterialTheme.typography.h4
            )
            TuningValue(
                value = frequency,
                unit = TuningUnit.HERTZ,
                color = MaterialTheme.colors.onBackground,
                valueStyle = MaterialTheme.typography.h5,
                unitStyle = MaterialTheme.typography.h6
            )
        }

    @Composable
    fun RequestPermissionSnackbar(state: SnackbarHostState) {
        val message = stringResource(R.string.permission_needed)
        val action = stringResource(R.string.give_permission)

        LaunchedEffect(state) {
            val result = state.showSnackbar(message, action, SnackbarDuration.Indefinite)
            if (result == SnackbarResult.ActionPerformed) {
                permissionManager.showExternalAppSettings()
            }
        }
    }
}
