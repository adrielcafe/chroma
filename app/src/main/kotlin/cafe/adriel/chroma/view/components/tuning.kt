package cafe.adriel.chroma.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.chroma.model.tuner.ChromaticScale
import cafe.adriel.chroma.model.tuner.TuningDeviationPrecision
import cafe.adriel.chroma.model.tuner.TuningDeviationResult
import cafe.adriel.chroma.model.tuner.TuningUnit

@Composable
fun TuningNote(
    note: ChromaticScale,
    tone: String,
    semitone: String,
    basicMode: Boolean,
    modifier: Modifier = Modifier
) =
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

@Composable
fun TuningInfo(deviation: Int, frequency: String, color: Color, modifier: Modifier = Modifier) =
    Column(modifier = modifier) {
        TuningValue(
            value = deviation.toString(),
            unit = TuningUnit.CENTS,
            valueStyle = MaterialTheme.typography.h3,
            unitStyle = MaterialTheme.typography.h4,
            color = color
        )
        TuningValue(
            value = frequency,
            unit = TuningUnit.HERTZ,
            valueStyle = MaterialTheme.typography.h5,
            unitStyle = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground
        )
    }

@Composable
fun TuningValue(
    value: String,
    unit: String,
    valueStyle: TextStyle,
    unitStyle: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) =
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

@Composable
fun TuningDeviationBars(deviationResult: TuningDeviationResult, modifier: Modifier = Modifier) =
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        TuningDeviationPrecision.values().forEach { item ->
            TuningDeviationBar(
                color = item.color, height = item.barHeight,
                active = when (deviationResult) {
                    is TuningDeviationResult.NotDetected -> false
                    is TuningDeviationResult.Detected -> item == deviationResult.precision
                    is TuningDeviationResult.Animation -> item in setOf(
                        deviationResult.negativePrecision,
                        deviationResult.positivePrecision
                    )
                }
            )
        }
    }

@Composable
fun TuningDeviationBar(color: Color, height: Dp, active: Boolean) =
    Spacer(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .size(width = 3.dp, height = height)
            .background(
                if (active) color
                else MaterialTheme.colors.onBackground.copy(alpha = .2f)
            )
    )
