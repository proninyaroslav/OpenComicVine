package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun LazyHorizontalCardGrid(
    modifier: Modifier = Modifier,
    rowCount: Int,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    state: LazyGridState = rememberLazyGridState(),
    content: LazyGridScope.() -> Unit,
) {
    BoxWithConstraints {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(rowCount),
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            content = content,
            contentPadding = contentPadding,
            state = state,
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun PreviewLazyHorizontalCardGrid_Compact() {
    OpenComicVineTheme {
        LazyHorizontalCardGrid(rowCount = 2) {
            items(10) {
                Card(
                    modifier = Modifier.defaultMinSize(minWidth = CardCellSize.Adaptive.Small.minSize)
                ) {}
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 600)
@Composable
fun PreviewLazyHorizontalCardGrid_Medium() {
    OpenComicVineTheme {
        LazyHorizontalCardGrid(rowCount = 2) {
            items(10) {
                Card(
                    modifier = Modifier.defaultMinSize(minWidth = CardCellSize.Adaptive.Small.minSize)
                ) {}
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 840)
@Composable
fun PreviewLazyHorizontalCardGrid_Expanded() {
    OpenComicVineTheme {
        LazyHorizontalCardGrid(rowCount = 2) {
            items(10) {
                Card(
                    modifier = Modifier.defaultMinSize(minWidth = CardCellSize.Adaptive.Small.minSize)
                ) {}
            }
        }
    }
}