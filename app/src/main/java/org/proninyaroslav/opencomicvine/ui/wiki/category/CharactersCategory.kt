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

package org.proninyaroslav.opencomicvine.ui.wiki.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.item.CharacterItem
import org.proninyaroslav.opencomicvine.model.paging.wiki.WikiEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState
import org.proninyaroslav.opencomicvine.ui.wiki.WikiErrorView

@Composable
fun CharactersCategory(
    characters: LazyPagingItems<CharacterItem>,
    toMediatorError: (LoadState.Error) -> WikiEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onCharacterClicked: (characterId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.characters),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = characters.rememberLazyListState(),
            loadState = characters.loadState,
            isEmpty = characters.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_face_24,
                    label = stringResource(R.string.no_characters),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    CharacterCard(
                        characterInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                WikiErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_characters_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_characters_list_error_template, it)
                    },
                    onRetry = { characters.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center),
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = characters.itemCount,
                key = { index -> characters[index]?.id ?: index },
            ) { index ->
                characters[index]?.let {
                    CharacterCard(
                        characterInfo = it.info,
                        onClick = { onCharacterClicked(it.id) },
                    )
                }
            }
        }
    }
}
