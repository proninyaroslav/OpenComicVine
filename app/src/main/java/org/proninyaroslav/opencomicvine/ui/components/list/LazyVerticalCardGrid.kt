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
fun LazyVerticalCardGrid(
    modifier: Modifier = Modifier,
    cellSize: CardCellSize = CardCellSize.Adaptive.Small,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyGridState = rememberLazyGridState(),
    content: LazyGridScope.() -> Unit,
) {
    BoxWithConstraints {
        val isLarge = maxWidth > 600.dp
        LazyVerticalGrid(
            columns = when (cellSize) {
                is CardCellSize.Adaptive -> {
                    GridCells.Adaptive(
                        if (isLarge) {
                            cellSize.getLargerSize()
                        } else {
                            cellSize.minSize
                        }
                    )
                }
                is CardCellSize.Fixed -> GridCells.Fixed(cellSize.count)
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
            contentPadding = contentPadding,
            state = state,
            modifier = modifier,
        )
    }
}

private fun CardCellSize.Adaptive.getLargerSize() = when (this) {
    CardCellSize.Adaptive.Small -> CardCellSize.Adaptive.Medium
    CardCellSize.Adaptive.Medium -> CardCellSize.Adaptive.Large
    CardCellSize.Adaptive.Large -> this
}.minSize

@Preview(showBackground = true, widthDp = 400)
@Composable
fun PreviewLazyVerticalCardGrid_Compact() {
    OpenComicVineTheme {
        LazyVerticalCardGrid {
            items(10) {
                Card(
                    modifier = Modifier.defaultMinSize(minHeight = CardCellSize.Adaptive.Small.minSize)
                ) {}
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 600)
@Composable
fun PreviewLazyVerticalCardGrid_Medium() {
    OpenComicVineTheme {
        LazyVerticalCardGrid {
            items(10) {
                Card(
                    modifier = Modifier.defaultMinSize(minHeight = CardCellSize.Adaptive.Small.minSize)
                ) {}
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 840)
@Composable
fun PreviewLazyVerticalCardGrid_Expanded() {
    OpenComicVineTheme {
        LazyVerticalCardGrid {
            items(10) {
                Card(
                    modifier = Modifier.defaultMinSize(minHeight = CardCellSize.Adaptive.Small.minSize)
                ) {}
            }
        }
    }
}