package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

private val Elevation = 6.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshIndicatorMaterial3(
    refreshing: Boolean,
    state: PullRefreshState,
    modifier: Modifier = Modifier,
) {
    PullRefreshIndicator(
        refreshing = refreshing,
        state = state,
        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun PreviewSwipeRefreshIndicatorMaterial3() {
    OpenComicVineTheme {
        PullRefreshIndicatorMaterial3(
            refreshing = true,
            state = rememberPullRefreshState(refreshing = true, onRefresh = {}),
        )
    }
}