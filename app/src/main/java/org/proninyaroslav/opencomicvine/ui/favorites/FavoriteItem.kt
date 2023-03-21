package org.proninyaroslav.opencomicvine.ui.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.components.FavoriteBox
import org.proninyaroslav.opencomicvine.ui.components.FavoriteFilledTonalButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyGridItemScope.FavoriteItem(
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BaseFavoriteItem(
        onFavoriteClick = onFavoriteClick,
        modifier = modifier.animateItemPlacement(),
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.FavoriteItem(
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BaseFavoriteItem(
        onFavoriteClick = onFavoriteClick,
        modifier = modifier.animateItemPlacement(),
        content = content,
    )
}

@Composable
private fun BaseFavoriteItem(
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FavoriteBox(
        icon = {
            FavoriteFilledTonalButton(
                isFavorite = true,
                onClick = onFavoriteClick,
            )
        },
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewFavoriteItem() {
    OpenComicVineTheme {
        BaseFavoriteItem(
            onFavoriteClick = {},
        ) {
            Card(modifier = Modifier.size(150.dp, 200.dp)) {}
        }
    }
}