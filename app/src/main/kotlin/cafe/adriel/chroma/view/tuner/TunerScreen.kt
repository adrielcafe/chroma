package cafe.adriel.chroma.view.tuner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.chroma.R
import cafe.adriel.chroma.ktx.openExternalAppSettings
import cafe.adriel.chroma.ktx.rateApp
import cafe.adriel.chroma.ktx.shareApp
import cafe.adriel.chroma.ktx.toggle
import cafe.adriel.chroma.model.settings.AccidentalOption
import cafe.adriel.chroma.model.settings.DeviationPrecisionOption
import cafe.adriel.chroma.model.settings.NotationOption
import cafe.adriel.chroma.model.settings.PitchDetectionAlgorithmOption
import cafe.adriel.chroma.model.settings.Settings
import cafe.adriel.chroma.model.tuner.Tuning
import cafe.adriel.chroma.model.tuner.TuningDeviationResult
import cafe.adriel.chroma.view.ComposableScreen
import cafe.adriel.chroma.view.components.AboutDialog
import cafe.adriel.chroma.view.components.ActionPreference
import cafe.adriel.chroma.view.components.DonateDialog
import cafe.adriel.chroma.view.components.MessageSnackbar
import cafe.adriel.chroma.view.components.RequestPermissionSnackbar
import cafe.adriel.chroma.view.components.SelectPreference
import cafe.adriel.chroma.view.components.SwitchPreference
import cafe.adriel.chroma.view.components.TuningDeviationBars
import cafe.adriel.chroma.view.components.TuningInfo
import cafe.adriel.chroma.view.components.TuningNote
import cafe.adriel.chroma.view.theme.ChromaTheme

class TunerScreen(
    private val viewModel: TunerViewModel
) : ComposableScreen {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val screenState by viewModel.state.collectAsState()
        val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ChromaTheme {
            BackdropScaffold(
                appBar = {
                    TunerTopBar(onSettingsClicked = { scaffoldState.toggle(scope) })
                },
                backLayerContent = {
                    TunerContent(screenState.tuning, screenState.settings)

                    if (screenState.hasRequiredPermissions.not()) {
                        RequestPermissionSnackbar(scaffoldState.snackbarHostState, context::openExternalAppSettings)
                    }

                    screenState.message?.let { message ->
                        MessageSnackbar(message, scaffoldState.snackbarHostState, viewModel::consumeMessage)
                    }
                },
                frontLayerContent = {
                    TunerSettings(screenState.settings, screenState.isBillingSupported)
                },
                headerHeight = Dp.Hairline,
                scaffoldState = scaffoldState
            )
        }
    }

    @Composable
    private fun TunerTopBar(onSettingsClicked: () -> Unit) =
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
                    onClick = onSettingsClicked,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
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
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (noteRef, deviationBarsRef, infoRef) = createRefs()
            val verticalGuideline = createGuidelineFromTop(.55f)

            if (tuning.deviation is TuningDeviationResult.Detected && tuning.note != null) {
                TuningNote(
                    note = tuning.note,
                    tone = tuning.getTone(settings),
                    accidental = tuning.getSemitoneSymbolRes(settings)?.let { painterResource(it) },
                    advancedMode = settings.advancedMode,
                    modifier = Modifier.constrainAs(noteRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                        bottom.linkTo(deviationBarsRef.top)
                    }
                )
            }

            TuningDeviationBars(
                deviationResult = tuning.deviation,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 24.dp)
                    .constrainAs(deviationBarsRef) {
                        centerAround(verticalGuideline)
                    }
            )

            if (tuning.deviation is TuningDeviationResult.Detected && tuning.note != null && settings.advancedMode) {
                TuningInfo(
                    deviation = tuning.deviation.value,
                    frequency = tuning.formattedFrequency,
                    color = tuning.deviation.precision.color,
                    modifier = Modifier.constrainAs(infoRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(deviationBarsRef.bottom)
                    }
                )
            }
        }

    @Composable
    private fun TunerSettings(settings: Settings, isBillingSupported: Boolean) {
        val (showAboutDialog, setAboutDialogVisible) = remember { mutableStateOf(false) }
        val (showDonateDialog, setDonateDialogVisible) = remember { mutableStateOf(false) }
        val context = LocalContext.current

        LazyColumn {
            item {
                SwitchPreference(
                    title = stringResource(R.string.advanced_mode),
                    subtitle = stringResource(R.string.show_secondary_data),
                    checked = settings.advancedMode,
                    onChanged = {
                        viewModel.updateSettings(settings.copy(advancedMode = settings.advancedMode.not()))
                    }
                )
                SwitchPreference(
                    title = stringResource(R.string.noise_suppressor),
                    subtitle = stringResource(R.string.removes_background_noise),
                    checked = settings.noiseSuppressor,
                    onChanged = {
                        viewModel.updateSettings(settings.copy(noiseSuppressor = settings.noiseSuppressor.not()))
                    }
                )
                SelectPreference(
                    title = stringResource(NotationOption.titleRes),
                    selected = settings.notation,
                    options = NotationOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(notation = it))
                    }
                )
                SelectPreference(
                    title = stringResource(AccidentalOption.titleRes),
                    selected = settings.accidental,
                    options = AccidentalOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(accidental = it))
                    }
                )
                SelectPreference(
                    title = stringResource(DeviationPrecisionOption.titleRes),
                    selected = settings.deviationPrecision,
                    options = DeviationPrecisionOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(deviationPrecision = it))
                    }
                )
                SelectPreference(
                    title = stringResource(PitchDetectionAlgorithmOption.titleRes),
                    selected = settings.pitchDetectionAlgorithm,
                    options = PitchDetectionAlgorithmOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(pitchDetectionAlgorithm = it))
                    }
                )
                Divider(
                    color = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium),
                    thickness = .5.dp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )
                ActionPreference(
                    title = stringResource(R.string.about),
                    icon = Icons.Default.Person,
                    onClick = { setAboutDialogVisible(true) }
                )
                if (isBillingSupported) {
                    ActionPreference(
                        title = stringResource(R.string.buy_me_coffee),
                        icon = Icons.Default.FreeBreakfast,
                        onClick = { setDonateDialogVisible(true) }
                    )
                }
                ActionPreference(
                    title = stringResource(R.string.share),
                    icon = Icons.Default.Share,
                    onClick = { context.shareApp() }
                )
                ActionPreference(
                    title = stringResource(R.string.rate_review),
                    icon = Icons.Default.Star,
                    onClick = { context.rateApp() }
                )
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }

        if (showAboutDialog) {
            AboutDialog(onClose = { setAboutDialogVisible(false) })
        }

        if (showDonateDialog) {
            DonateDialog(
                onDonate = viewModel::donate,
                onClose = { setDonateDialogVisible(false) }
            )
        }
    }
}
