package cafe.adriel.chroma.view.compose.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ChromaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ChromaColors(),
        typography = ChromaTypography(),
        content = content
    )
}
