package org.proninyaroslav.opencomicvine.ui.favorites

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.components.FilterIconButton
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryAppBar
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCategoryAppBar(
    title: @Composable () -> Unit,
    onFilterClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CategoryAppBar(
        title = title,
        actions = { FilterIconButton(onClick = onFilterClick) },
        scrollBehavior = scrollBehavior,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFavoriteCategoryAppBar() {
    OpenComicVineTheme {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
        FavoriteCategoryAppBar(
            title = { Text("Title") },
            onFilterClick = {},
            scrollBehavior = scrollBehavior,
            onBackButtonClicked = {},
        )
    }
}