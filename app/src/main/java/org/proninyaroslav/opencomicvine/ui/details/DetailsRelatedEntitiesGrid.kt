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

package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingConfig
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.ui.components.list.PagingHorizontalCardGrid
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import kotlin.math.max
import kotlin.math.min

fun buildRelatedEntitiesPagingConfig(totalSize: Int?): PagingConfig {
    val size = totalSize ?: PagingConfig.MAX_SIZE_UNBOUNDED
    val pageSize = min(size, ComicVineSource.DEFAULT_PAGE_SIZE)
    return PagingConfig(
        pageSize = pageSize,
        prefetchDistance = pageSize,
        maxSize = max(size, pageSize + 2 * pageSize),
    )
}

@Composable
fun DetailsRelatedEntitiesGrid(
    state: LazyGridState?,
    loadState: CombinedLoadStates?,
    items: LazyGridScope.() -> Unit,
    loadingPlaceholder: LazyGridScope.(rowsCount: Int) -> Unit,
    placeholder: @Composable () -> Unit,
    onError: @Composable BoxScope.(state: LoadState.Error) -> Unit,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    rowsCount: Int = 2,
) {
    if (state == null || loadState == null) {
        LoadingPlaceholder(
            rows = rowsCount,
            items = loadingPlaceholder,
            modifier = modifier,
        )
    } else {
        PagingHorizontalCardGrid(
            rowCount = rowsCount,
            contentPadding = PaddingValues(16.dp),
            state = state,
            isEmpty = isEmpty,
            loadState = loadState,
            placeholder = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    placeholder()
                }
            },
            loadingPlaceholder = {
                LoadingPlaceholder(
                    rows = rowsCount,
                    items = loadingPlaceholder,
                )
            },
            onError = { s ->
                Box(modifier = Modifier.fillMaxSize()) {
                    onError(s)
                }
            },
            modifier = modifier.fillMaxWidth(),
            content = items,
        )
    }
}

@Composable
private fun LoadingPlaceholder(
    rows: Int,
    items: LazyGridScope.(rowsCount: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(rows)
    }
}

@Preview
@Composable
private fun PreviewDetailsRelatedEntitiesGrid() {
    OpenComicVineTheme {
        DetailsRelatedEntitiesGrid(
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
            isEmpty = false,
            items = {
                items(5) {
                    Card(modifier = Modifier.width(96.dp)) {}
                }
            },
            loadingPlaceholder = {},
            placeholder = {},
            onError = {},
            modifier = Modifier.height(320.dp),
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsRelatedEntitiesGrid_Loading() {
    OpenComicVineTheme {
        DetailsRelatedEntitiesGrid(
            state = null,
            loadState = null,
            isEmpty = true,
            items = {},
            loadingPlaceholder = { rows ->
                items(rows * 3) {
                    Card(modifier = Modifier.width(96.dp)) {}
                }
            },
            placeholder = {},
            onError = {},
            modifier = Modifier.height(320.dp),
        )
    }
}
