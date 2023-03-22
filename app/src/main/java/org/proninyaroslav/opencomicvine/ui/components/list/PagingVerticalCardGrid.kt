/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.ui.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.AnimatedSlideContent
import org.proninyaroslav.opencomicvine.ui.components.PullRefreshIndicatorMaterial3
import org.proninyaroslav.opencomicvine.ui.components.error.RetryableErrorPage
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PagingVerticalCardGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState,
    loadState: CombinedLoadStates,
    isEmpty: Boolean,
    cellSize: CardCellSize = CardCellSize.Adaptive.Small,
    placeholder: @Composable () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onError: @Composable (state: LoadState.Error, fullscreen: Boolean) -> Unit,
    onRefresh: () -> Unit = {},
    refreshing: Boolean = false,
    content: LazyGridScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val showScrollUpButton by remember(state) {
        derivedStateOf { state.firstVisibleItemIndex > 0 }
    }
    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = onRefresh)

    Box(Modifier.pullRefresh(refreshState)) {
        LazyVerticalCardGrid(
            cellSize = cellSize,
            contentPadding = contentPadding,
            state = state,
            modifier = modifier.fillMaxSize(),
        ) {
            loadState.mediator?.refresh?.let {
                if (!isEmpty) {
                    mapState(
                        loadState = it,
                        alignment = Alignment.Top,
                        onError = { error -> onError(error, fullscreen = false) },
                        gridState = state,
                        coroutineScope = coroutineScope,
                    )
                }
            }
            mapState(
                loadState = loadState.prepend,
                alignment = Alignment.Top,
                onError = { error -> onError(error, fullscreen = false) },
                gridState = state,
                coroutineScope = coroutineScope,
            )
            content()
            mapState(
                loadState = loadState.append,
                alignment = Alignment.Bottom,
                onError = { error -> onError(error, fullscreen = false) },
                gridState = state,
                coroutineScope = coroutineScope,
            )
            if (showScrollUpButton) {
                item {
                    Spacer(modifier = Modifier.height(ScrollUpButtonHeight + 16.dp))
                }
            }
        }

        PullRefreshIndicatorMaterial3(
            refreshing = refreshing,
            state = refreshState,
            modifier = Modifier
                .padding(contentPadding)
                .align(Alignment.TopCenter),
        )
    }

    FullscreenBox(
        modifier = modifier,
    ) {
        if (isEmpty) {
            when (loadState.refresh) {
                is LoadState.Loading -> CircularProgressIndicator()
                is LoadState.Error -> {
                    onError(loadState.refresh as LoadState.Error, fullscreen = true)
                }
                else -> placeholder()
            }
        }

        ScrollUpButton(
            visible = showScrollUpButton,
            onClick = {
                coroutineScope.launch { state.scrollToItem(index = 0) }
            },
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private inline fun FullscreenBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        content = content,
        modifier = modifier.fillMaxSize(),
    )
}

private fun LazyGridScope.mapState(
    loadState: LoadState,
    alignment: Alignment.Vertical,
    onError: @Composable (LoadState.Error) -> Unit,
    gridState: LazyGridState,
    coroutineScope: CoroutineScope,
) {
    item(
        span = { GridItemSpan(maxLineSpan) }
    ) {
        AnimatedSlideContent(
            targetState = loadState,
            alignment = alignment,
        ) { state ->
            when (state) {
                is LoadState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                is LoadState.Error -> {
                    SideEffect {
                        coroutineScope.launch {
                            if (alignment == Alignment.Bottom) {
                                gridState.lastVisibleItemIndex
                            } else {
                                0
                            }?.let {
                                gridState.animateScrollToItem(index = it)
                            }
                        }
                    }
                    onError(state)
                }
                else -> {}
            }
        }
    }
}

private val LazyGridState.lastVisibleItemIndex: Int?
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index

@Preview(name = "Refresh loading", showSystemUi = true)
@Composable
private fun PreviewPagingVerticalCardGrid_RefreshLoading() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
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
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {}
    }
}

@Preview(name = "Append loading", showSystemUi = true)
@Composable
private fun PreviewPagingVerticalCardGrid_AppendLoading() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.Loading,
                prepend = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {
            items(3) {
                Card(modifier = Modifier.defaultMinSize(minHeight = 200.dp)) {}
            }
        }
    }
}

@Preview(name = "Prepend loading", showSystemUi = true)
@Composable
fun PreviewPagingVerticalCardGrid_PrependLoading() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.Loading,
                source = LoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {
            items(3) {
                Card(modifier = Modifier.defaultMinSize(minHeight = 200.dp)) {}
            }
        }
    }
}

@Preview(name = "Refresh local error", showSystemUi = true)
@Composable
fun PreviewPagingVerticalCardGrid_RefreshLocalError() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.Error(IOException()),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.Error(IOException()),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {}
    }
}

@Preview(name = "Refresh remote error", showSystemUi = true)
@Composable
fun PreviewPagingVerticalCardGrid_RefreshRemoteError() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
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
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {
            items(3) {
                Card(modifier = Modifier.defaultMinSize(minHeight = 200.dp)) {}
            }
        }
    }
}

@Preview(name = "Append error", showSystemUi = true)
@Composable
fun PreviewPagingVerticalCardGrid_AppendError() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.Error(IOException()),
                prepend = LoadState.NotLoading(false),
                source = LoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {
            items(3) {
                Card(modifier = Modifier.defaultMinSize(minHeight = 200.dp)) {}
            }
        }
    }
}

@Preview(name = "Prepend error", showSystemUi = true)
@Composable
fun PreviewPagingVerticalCardGrid_PrependError() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
            state = rememberLazyGridState(),
            loadState = CombinedLoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.Error(IOException()),
                source = LoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                ),
            ),
            isEmpty = false,
            placeholder = {},
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {
            items(3) {
                Card(modifier = Modifier.defaultMinSize(minHeight = 200.dp)) {}
            }
        }
    }
}

@Preview(name = "Empty list", showSystemUi = true)
@Composable
fun PreviewPagingVerticalCardGrid_EmptyList() {
    OpenComicVineTheme {
        PagingVerticalCardGrid(
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
            isEmpty = true,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_menu_book_24,
                    label = "Empty list",
                )
            },
            onError = { state, fullscreen ->
                RetryableErrorPage(
                    errorMessage = "${state.error}",
                    onRetry = {},
                    onCopyStackTrace = {},
                    compact = !fullscreen,
                )
            },
        ) {}
    }
}
