package org.proninyaroslav.opencomicvine.ui.components.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CategoriesList(
    modifier: Modifier = Modifier,
    isExpandedWidth: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns = if (isExpandedWidth) GridCells.Adaptive(300.dp) else GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize(),
        content = content,
    )
}

@Preview
@Composable
fun PreviewCategoriesList() {
    OpenComicVineTheme {
        CategoriesList(
            isExpandedWidth = false,
        ) {
            items(3) {
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                ) {}
            }
        }
    }
}

@Preview("Expanded", widthDp = 1000)
@Composable
fun PreviewCategoriesList_Expanded() {
    OpenComicVineTheme {
        CategoriesList(
            isExpandedWidth = true,
        ) {
            items(3) {
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                ) {}
            }
        }
    }
}