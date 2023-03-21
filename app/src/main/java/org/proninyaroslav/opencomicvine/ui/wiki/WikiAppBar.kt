package org.proninyaroslav.opencomicvine.ui.wiki

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    MediumTopAppBar(
        title = { Text(stringResource(R.string.wiki)) },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewWikiAppBar() {
    OpenComicVineTheme {
        WikiAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewWikiAppBarDark() {
    OpenComicVineTheme(darkTheme = true) {
        WikiAppBar()
    }
}