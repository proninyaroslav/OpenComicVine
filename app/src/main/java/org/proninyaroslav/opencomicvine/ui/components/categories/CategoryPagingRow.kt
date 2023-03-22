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

package org.proninyaroslav.opencomicvine.ui.components.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import org.proninyaroslav.opencomicvine.ui.components.list.PagingCardRow

@Composable
fun CategoryPagingRow(
    modifier: Modifier = Modifier,
    state: LazyListState,
    loadState: CombinedLoadStates,
    isEmpty: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    placeholder: @Composable () -> Unit,
    loadingPlaceholder: @Composable RowScope.() -> Unit,
    onError: @Composable BoxScope.(state: LoadState.Error) -> Unit,
    onLoadMore: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    PagingCardRow(
        state = state,
        loadState = loadState,
        isEmpty = isEmpty,
        contentPadding = contentPadding,
        placeholder = placeholder,
        loadingPlaceholder = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                loadingPlaceholder()
            }
        },
        onError = { s ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                onError(s)
            }
        },
        modifier = modifier,
    ) {
        item {
            Spacer(modifier = Modifier.width(16.dp))
        }
        content()
        item {
            LoadMoreButton(
                onClick = onLoadMore,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
