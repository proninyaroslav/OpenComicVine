package org.proninyaroslav.opencomicvine.ui.settings

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.BackButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(
    modifier: Modifier = Modifier,
    onBackButtonClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeTopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = { BackButton(onClick = onBackButtonClicked) },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSettingsAppBar() {
    OpenComicVineTheme {
        SettingsAppBar(
            onBackButtonClicked = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSettingsAppBarDark() {
    OpenComicVineTheme(darkTheme = true) {
        SettingsAppBar(
            onBackButtonClicked = {},
        )
    }
}