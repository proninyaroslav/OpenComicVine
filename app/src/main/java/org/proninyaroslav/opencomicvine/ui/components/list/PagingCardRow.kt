package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
fun PagingCardRow(
    modifier: Modifier = Modifier,
    state: LazyListState,
    loadState: CombinedLoadStates,
    isEmpty: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    placeholder: @Composable () -> Unit,
    loadingPlaceholder: @Composable BoxScope.() -> Unit,
    onError: @Composable BoxScope.(state: LoadState.Error) -> Unit,
    content: LazyListScope.() -> Unit,
) {
    if (!isEmpty) {
        LazyCardRow(
            contentPadding = contentPadding,
            state = state,
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
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
fun PreviewPagingCardRow() {
    OpenComicVineTheme {
        PagingCardRow(
            state = rememberLazyListState(),
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
fun PreviewPagingCardRow_Refresh() {
    OpenComicVineTheme {
        PagingCardRow(
            state = rememberLazyListState(),
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
fun PreviewPagingCardRow_RefreshError() {
    OpenComicVineTheme {
        PagingCardRow(
            state = rememberLazyListState(),
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