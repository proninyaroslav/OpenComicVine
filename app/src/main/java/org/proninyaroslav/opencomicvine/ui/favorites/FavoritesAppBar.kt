package org.proninyaroslav.opencomicvine.ui.favorites

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    MediumTopAppBar(
        title = { Text(stringResource(R.string.favorites)) },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFavoritesAppBar() {
    OpenComicVineTheme {
        FavoritesAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFavoritesAppBarDark() {
    OpenComicVineTheme {
        FavoritesAppBar()
    }
}