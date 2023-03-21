package org.proninyaroslav.opencomicvine.ui.components.categories

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun LoadMoreButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            painterResource(R.drawable.ic_more_horiz_48),
            contentDescription = stringResource(R.string.load_more),
            modifier = Modifier.size(36.dp),
        )
    }
}

@Preview
@Composable
fun PreviewLoadMoreButton() {
    OpenComicVineTheme {
        LoadMoreButton(onClick = {})
    }
}