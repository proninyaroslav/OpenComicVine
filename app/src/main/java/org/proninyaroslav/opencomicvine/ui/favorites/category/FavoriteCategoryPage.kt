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

package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesItem
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDrawer
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.PagingVerticalCardGrid
import org.proninyaroslav.opencomicvine.ui.favorites.*
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.*
import org.proninyaroslav.opencomicvine.ui.fling
import org.proninyaroslav.opencomicvine.ui.rememberLazyGridState
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.viewmodel.*

enum class FavoriteCategoryPageType {
    Characters,
    Issues,
    Volumes,
    Concepts,
    Locations,
    Movies,
    Objects,
    People,
    StoryArcs,
    Teams,
}

interface FavoriteCategoryPage {
    data class ErrorMessageTemplates(
        @StringRes val fetchTemplate: Int,
        @StringRes val saveTemplate: Int,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : FavoritesItem> FavoriteCategoryPage(
    modifier: Modifier = Modifier,
    type: FavoriteCategoryPageType,
    title: @Composable () -> Unit,
    itemCard: @Composable (item: T) -> Unit,
    emptyListPlaceholder: @Composable () -> Unit,
    items: LazyPagingItems<T>,
    errorMessageTemplates: FavoriteCategoryPage.ErrorMessageTemplates,
    cellSize: CardCellSize = CardCellSize.Adaptive.Small,
    viewModel: FavoriteCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    filterViewModel: FavoritesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBackButtonClicked: () -> Unit,
) {
    val filterState by filterViewModel.state.collectAsStateWithLifecycle()
    val showApplyButton by remember(filterState) {
        derivedStateOf {
            when (val s = filterState) {
                is FavoritesFilterState.SortChanged -> s.isNeedApply
                else -> false
            }
        }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarState = LocalAppSnackbarState.current

    LaunchedEffect(networkConnection) {
        networkConnection.effect.collect { effect ->
            when (effect) {
                NetworkEffect.Reestablished -> items.retry()
            }
        }
    }

    LaunchedEffect(filterViewModel) {
        filterViewModel.effect.collect { effect ->
            when (effect) {
                FavoritesFilterEffect.Applied -> items.refresh()
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteCategoryPageEffect.Refresh -> {
                    coroutineScope.launch { items.refresh() }
                }
            }
        }
    }

    LaunchedEffect(favoritesViewModel) {
        favoritesViewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.Added -> {}
                is FavoritesEffect.Removed -> coroutineScope.launch {
                    val res = snackbarState.showSnackbar(
                        context.getString(R.string.removed_from_favorites_message),
                        context.getString(R.string.undo),
                        duration = SnackbarDuration.Short,
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        favoritesViewModel.event(
                            FavoritesEvent.SwitchFavorite(
                                entityId = effect.entityId,
                                entityType = effect.entityType,
                            )
                        )
                    }
                }
                is FavoritesEffect.SwitchFavoriteFailed -> coroutineScope.launch {
                    snackbarState.showSnackbar(
                        context.getString(
                            R.string.error_add_delete_from_favorites,
                            effect.error,
                        )
                    )
                }
            }
        }
    }

    FilterDrawer(
        drawerContent = {
            favoritesFilter(
                sort = filterState.sort,
                onSortChanged = {
                    filterViewModel.event(
                        FavoritesFilterEvent.ChangeSort(sort = it)
                    )
                },
            )
        },
        showApplyButton = showApplyButton,
        drawerState = drawerState,
        onClose = { coroutineScope.launch { drawerState.close() } },
        onApply = {
            filterViewModel.event(FavoritesFilterEvent.Apply)
            coroutineScope.launch { drawerState.close() }
        },
    ) {
        Scaffold(
            topBar = {
                FavoriteCategoryAppBar(
                    title = title,
                    onFilterClick = { coroutineScope.launch { drawerState.fling() } },
                    scrollBehavior = scrollBehavior,
                    onBackButtonClicked = onBackButtonClicked,
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            val direction = LocalLayoutDirection.current
            val newContentPadding by remember(contentPadding) {
                derivedStateOf {
                    contentPadding.removeBottomPadding(
                        direction = direction,
                        extraHorizontal = 16.dp,
                        extraVertical = 16.dp,
                    )
                }
            }
            PagingVerticalCardGrid(
                state = items.rememberLazyGridState(),
                loadState = items.loadState,
                isEmpty = items.itemCount == 0,
                placeholder = emptyListPlaceholder,
                contentPadding = newContentPadding,
                cellSize = cellSize,
                onRefresh = { items.refresh() },
                onError = { state, fullscreen ->
                    FavoritesErrorView(
                        state = state,
                        toMediatorError = viewModel::toMediatorError,
                        formatFetchErrorMessage = {
                            context.getString(errorMessageTemplates.fetchTemplate, it)
                        },
                        formatSaveErrorMessage = {
                            context.getString(errorMessageTemplates.saveTemplate, it)
                        },
                        onRetry = { items.retry() },
                        onReport = { viewModel.event(FavoriteCategoryPageEvent.ErrorReport(it)) },
                        compact = !fullscreen,
                    )
                },
                modifier = modifier,
            ) {
                items(
                    count = items.itemCount,
                    key = { index -> items[index]?.id ?: index },
                ) { index ->
                    items[index]?.let {
                        FavoriteItem(
                            onFavoriteClick = {
                                favoritesViewModel.event(
                                    FavoritesEvent.SwitchFavorite(
                                        entityId = it.id,
                                        entityType = type.toEntityType(),
                                    )
                                )
                            },
                        ) {
                            itemCard(it)
                        }
                    }
                }
            }
        }
    }
}

private fun FavoriteCategoryPageType.toEntityType(): FavoriteInfo.EntityType = when (this) {
    FavoriteCategoryPageType.Characters -> FavoriteInfo.EntityType.Character
    FavoriteCategoryPageType.Issues -> FavoriteInfo.EntityType.Issue
    FavoriteCategoryPageType.Volumes -> FavoriteInfo.EntityType.Volume
    FavoriteCategoryPageType.Concepts -> FavoriteInfo.EntityType.Concept
    FavoriteCategoryPageType.Locations -> FavoriteInfo.EntityType.Location
    FavoriteCategoryPageType.Movies -> FavoriteInfo.EntityType.Movie
    FavoriteCategoryPageType.Objects -> FavoriteInfo.EntityType.Object
    FavoriteCategoryPageType.People -> FavoriteInfo.EntityType.Person
    FavoriteCategoryPageType.StoryArcs -> FavoriteInfo.EntityType.StoryArc
    FavoriteCategoryPageType.Teams -> FavoriteInfo.EntityType.Team
}
