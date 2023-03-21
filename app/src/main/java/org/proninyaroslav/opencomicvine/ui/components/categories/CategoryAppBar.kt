package org.proninyaroslav.opencomicvine.ui.components.categories

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.BackButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBackButtonClicked: () -> Unit,
) {
    LargeTopAppBar(
        title = title,
        navigationIcon = { BackButton(onClick = onBackButtonClicked) },
        actions = actions,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewCategoryAppBar() {
    OpenComicVineTheme {
        CategoryAppBar(
            title = { Text(stringResource(R.string.characters)) },
            onBackButtonClicked = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewCategoryAppBarDark() {
    OpenComicVineTheme(darkTheme = true) {
        CategoryAppBar(
            title = { Text(stringResource(R.string.characters)) },
            onBackButtonClicked = {},
        )
    }
}