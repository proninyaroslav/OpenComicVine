package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import org.proninyaroslav.opencomicvine.ui.components.error.RetryableErrorPage
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@Composable
fun PagingHorizontalCardGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState,
    loadState: CombinedLoadStates,
    isEmpty: Boolean,
    rowCount: Int,
    placeholder: @Composable () -> Unit,
    loadingPlaceholder: @Composable BoxScope.() -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    onError: @Composable BoxScope.(state: LoadState.Error) -> Unit,
    content: LazyGridScope.() -> Unit,
) {
    if (!isEmpty) {
        LazyHorizontalCardGrid(
            rowCount = rowCount,
            contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            state = state,
            modifier = modifier,
            content = content,
        )
    }

    if (isEmpty) {
        when (loadState.refresh) {
            is LoadState.Loading -> Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                loadingPlaceholder()
            }
            is LoadState.Error -> Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                onError(loadState.refresh as LoadState.Error)
            }
            else -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                placeholder()
            }
        }
    }
}

@Preview
@Composable
fun PreviewPagingHorizontalCardGrid() {
    OpenComicVineTheme {
        PagingHorizontalCardGrid(
            rowCount = 2,
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            placeholder = {},
            loadingPlaceholder = {},
            onError = { state ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = true,
                )
            },
            isEmpty = false,
        ) {
            items(3) {
                Card(modifier = Modifier.size(150.dp, 200.dp)) {}
            }
        }
    }
}

@Preview("Refresh")
@Composable
fun PreviewPagingHorizontalCardGrid_Refresh() {
    OpenComicVineTheme {
        PagingHorizontalCardGrid(
            rowCount = 2,
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.Loading,
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.Loading,
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            placeholder = {},
            loadingPlaceholder = {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            onError = { state ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = true,
                )
            },
            isEmpty = true,
        ) {}
    }
}

@Preview(name = "Refresh error")
@Composable
fun PreviewPagingHorizontalCardGrid_RefreshError() {
    OpenComicVineTheme {
        PagingHorizontalCardGrid(
            rowCount = 2,
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.Error(IOException()),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
                mediator = LoadStates(
                    refresh = LoadState.Error(IOException()),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                )
            ),
            placeholder = {},
            loadingPlaceholder = {},
            onError = { state ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = true,
                )
            },
            isEmpty = true,
        ) {}
    }
}