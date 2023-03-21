package org.proninyaroslav.opencomicvine.ui.components.chip

import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun ChipFlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: Dp = 8.dp,
    crossAxisSpacing: Dp = 4.dp,
    content: @Composable () -> Unit,
) {
    FlowRow(
        mainAxisSpacing = mainAxisSpacing,
        crossAxisSpacing = crossAxisSpacing,
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
fun PreviewChipFlowRow() {
    OpenComicVineTheme {
        ChipFlowRow {
            SuggestionChip(
                label = { Text("Chip 1") },
                onClick = {},
            )
            SuggestionChip(
                label = { Text("Chip 2") },
                onClick = {},
            )
            SuggestionChip(
                label = { Text("Chip 3") },
                onClick = {},
            )
        }
    }
}