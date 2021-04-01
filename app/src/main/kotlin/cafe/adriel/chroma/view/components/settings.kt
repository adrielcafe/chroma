package cafe.adriel.chroma.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.chroma.view.theme.ChromaColors

interface SelectOption<T : Enum<T>> {

    val labelRes: Int

    val type: T
        get() = this as T
}

private enum class SelectionOptionPosition(val startPadding: Dp, val endPadding: Dp) {
    FIRST(startPadding = 18.dp, endPadding = 4.dp),
    MIDDLE(startPadding = 4.dp, endPadding = 4.dp),
    LAST(startPadding = 4.dp, endPadding = 18.dp)
}

@Composable
fun SwitchPreference(title: String, subtitle: String, checked: Boolean, onChanged: () -> Unit) =
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .weight(.8f)
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Title(value = title)
            Subtitle(value = subtitle)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onChanged() },
            colors = SwitchDefaults.colors(uncheckedThumbColor = ChromaColors.gray),
            modifier = Modifier
                .weight(.2f)
                .align(Alignment.CenterVertically)
        )
    }

@Composable
fun <T : SelectOption<T>> SelectPreference(title: String, selected: T, options: Array<T>, onSelected: (T) -> Unit) =
    Column(modifier = Modifier.fillMaxWidth()) {
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = options.indexOf(selected))

        Title(
            value = title,
            modifier = Modifier.padding(top = 24.dp, bottom = 6.dp, start = 24.dp, end = 24.dp)
        )
        LazyRow(state = listState) {
            items(
                items = options,
                key = { it.labelRes }
            ) { item ->
                SelectOption(
                    title = stringResource(item.labelRes),
                    selected = item == selected,
                    position = when (item) {
                        options.first() -> SelectionOptionPosition.FIRST
                        options.last() -> SelectionOptionPosition.LAST
                        else -> SelectionOptionPosition.MIDDLE
                    },
                    onSelected = { onSelected(item) }
                )
            }
        }
    }

@Composable
fun ActionPreference(title: String, icon: ImageVector, onClick: () -> Unit) =
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colors.onBackground
        )
        Title(
            value = title,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
    }

@Composable
private fun Title(value: String, modifier: Modifier = Modifier) =
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.subtitle2,
        modifier = modifier
    )

@Composable
private fun Subtitle(value: String) =
    Text(
        text = value,
        color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
        style = MaterialTheme.typography.caption
    )

@Composable
private fun SelectOption(title: String, selected: Boolean, position: SelectionOptionPosition, onSelected: () -> Unit) {
    val color by animateColorAsState(
        if (selected) MaterialTheme.colors.secondaryVariant
        else MaterialTheme.colors.primary
    )

    TextButton(
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        shape = CircleShape,
        onClick = onSelected,
        modifier = Modifier.padding(start = position.startPadding, end = position.endPadding)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.caption
        )
    }
}
