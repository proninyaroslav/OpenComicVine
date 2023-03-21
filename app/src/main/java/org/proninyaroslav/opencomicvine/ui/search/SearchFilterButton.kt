package org.proninyaroslav.opencomicvine.ui.search

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun SearchFilterAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painterResource(R.drawable.ic_filter_list_24),
            contentDescription = stringResource(R.string.filter),
        )
    }
}

@Preview
@Composable
fun PreviewSearchFilterAction() {
    OpenComicVineTheme {
        SearchFilterAction(onClick = {})
    }
}