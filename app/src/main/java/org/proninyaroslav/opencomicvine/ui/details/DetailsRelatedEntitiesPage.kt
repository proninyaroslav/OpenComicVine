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

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.item.BaseItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.ui.components.FavoriteBox
import org.proninyaroslav.opencomicvine.ui.components.FavoriteFilledTonalButton
import org.proninyaroslav.opencomicvine.ui.rememberLazyGridState

private const val TAG = "DetailsRelatedEntities"

interface DetailsRelatedEntitiesPage {
    data class ErrorMessageTemplates(
        @StringRes val fetchTemplate: Int,
    )
}

@Composable
fun <T : BaseItem> DetailsRelatedEntitiesPage(
    modifier: Modifier = Modifier,
    emptyListPlaceholder: @Composable () -> Unit,
    itemCard: @Composable (T) -> Unit,
    loadingItemCard: @Composable () -> Unit,
    items: LazyPagingItems<T>?,
    errorMessageTemplates: DetailsRelatedEntitiesPage.ErrorMessageTemplates,
    rowsCount: Int = 2,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onFavoriteClick: (Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
) {
    val context = LocalContext.current
    DetailsRelatedEntitiesGrid(
        rowsCount = rowsCount,
        state = items?.rememberLazyGridState(),
        loadState = items?.loadState,
        items = {
            items?.let {
                items(
                    items.itemCount,
                    key = { index -> items[index]?.id ?: index },
                ) { index ->
                    it[index]?.let {
                        val isFavorite by produceState(initialValue = false, it.isFavorite) {
                            it.isFavorite.collect { res ->
                                value = when (res) {
                                    is FavoriteFetchResult.Success -> res.isFavorite
                                    is FavoriteFetchResult.Failed.IO -> {
                                        Log.e(TAG, "Unable to get favorites status", res.exception)
                                        false
                                    }
                                }
                            }
                        }
                        FavoriteBox(
                            icon = {
                                FavoriteFilledTonalButton(
                                    isFavorite = isFavorite,
                                    onClick = { onFavoriteClick(it.id) },
                                )
                            },
                        ) {
                            itemCard(it)
                        }
                    }
                }
            }
        },
        isEmpty = items?.itemCount == 0,
        placeholder = emptyListPlaceholder,
        loadingPlaceholder = { rows ->
            items(rows * 3) {
                loadingItemCard()
            }
        },
        onError = { state ->
            DetailsErrorView(
                state = state,
                toSourceError = toSourceError,
                formatFetchErrorMessage = {
                    context.getString(errorMessageTemplates.fetchTemplate, it)
                },
                compact = true,
                onRefresh = { items?.retry() },
                onReport = onReport,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.Center)
                    .padding(16.dp),
            )
        },
        modifier = modifier,
    )
}
