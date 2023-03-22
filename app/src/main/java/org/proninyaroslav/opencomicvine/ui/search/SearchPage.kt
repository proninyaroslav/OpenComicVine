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

package org.proninyaroslav.opencomicvine.ui.search

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.data.SearchInfo
import org.proninyaroslav.opencomicvine.data.item.SearchItem
import org.proninyaroslav.opencomicvine.model.paging.SearchSource
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import org.proninyaroslav.opencomicvine.ui.*
import org.proninyaroslav.opencomicvine.ui.components.drawer.AdaptiveFilterDrawer
import org.proninyaroslav.opencomicvine.ui.components.drawer.AdaptiveFilterDrawerType
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.components.list.PagingVerticalCardGrid
import org.proninyaroslav.opencomicvine.ui.components.search_bar.*
import org.proninyaroslav.opencomicvine.ui.search.history.SearchHistory
import org.proninyaroslav.opencomicvine.ui.search.history.SearchHistoryEffect
import org.proninyaroslav.opencomicvine.ui.search.history.SearchHistoryEvent
import org.proninyaroslav.opencomicvine.ui.search.history.SearchHistoryViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.*

sealed interface SearchPage {
    data class Character(val characterId: Int) : SearchPage
    data class Issue(val issueId: Int) : SearchPage
    data class Volume(val volumeId: Int) : SearchPage
    data class Concept(val conceptId: Int) : SearchPage
    data class Location(val locationId: Int) : SearchPage
    data class Movie(val movieId: Int) : SearchPage
    data class Object(val objectId: Int) : SearchPage
    data class Person(val personId: Int) : SearchPage
    data class StoryArc(val storyArcId: Int) : SearchPage
    data class Team(val teamId: Int) : SearchPage
    data class Episode(val episodeId: Int) : SearchPage
    data class Series(val seriesId: Int) : SearchPage
    data class Video(val videoId: Int) : SearchPage
}

private const val TAG = "SearchPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    viewModel: SearchViewModel,
    favoritesViewModel: FavoritesViewModel,
    networkConnection: NetworkConnectionViewModel,
    filterViewModel: SearchFilterViewModel,
    searchHistoryViewModel: SearchHistoryViewModel,
    isExpandedWidth: Boolean,
    onLoadPage: (SearchPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchHistoryList by searchHistoryViewModel.searchHistoryList.collectAsStateWithLifecycle()
    val searchList = viewModel.searchList.collectAsLazyPagingItems()
    val filterState by filterViewModel.state.collectAsStateWithLifecycle()
    val isNeedApplyFilter by remember(filterState) {
        derivedStateOf {
            when (val s = filterState) {
                is SearchFilterState.FiltersChanged -> s.isNeedApply
                else -> false
            }
        }
    }
    var showFilterButton by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val snackbarState = LocalAppSnackbarState.current
    val scrollBehavior = rememberSearchTopAppBarScrollBehavior()

    LaunchedEffect(networkConnection) {
        networkConnection.effect.collect { effect ->
            when (effect) {
                NetworkEffect.Reestablished -> searchList.retry()
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SearchEffect.Refresh -> searchList.refresh()
            }
        }
    }

    LaunchedEffect(filterViewModel) {
        filterViewModel.effect.collect { effect ->
            when (effect) {
                SearchFilterEffect.Applied -> searchList.refresh()
            }
        }
    }

    LaunchedEffect(favoritesViewModel) {
        favoritesViewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.SwitchFavoriteFailed -> when (val error = effect.error) {
                    is FavoritesRepository.Result.Failed.IO -> coroutineScope.launch {
                        snackbarState.showSnackbar(
                            context.getString(
                                R.string.error_add_delete_from_favorites,
                                error.exception
                            )
                        )
                    }
                }
                is FavoritesEffect.Added -> {}
                is FavoritesEffect.Removed -> {}
            }
        }
    }

    LaunchedEffect(searchHistoryViewModel) {
        searchHistoryViewModel.effect.collect { effect ->
            when (effect) {
                is SearchHistoryEffect.AddToHistoryFailed -> when (val error = effect.error) {
                    is SearchHistoryRepository.Result.Failed.IO -> coroutineScope.launch {
                        snackbarState.showSnackbar(
                            context.getString(
                                R.string.error_add_to_search_history_template,
                                error.exception
                            )
                        )
                    }
                }
                is SearchHistoryEffect.DeleteFromHistoryFailed -> when (val error = effect.error) {
                    is SearchHistoryRepository.Result.Failed.IO -> coroutineScope.launch {
                        snackbarState.showSnackbar(
                            context.getString(
                                R.string.error_delete_from_search_history_template,
                                error.exception
                            )
                        )
                    }
                }
                is SearchHistoryEffect.AddedToHistory -> {}
                is SearchHistoryEffect.RemovedFromHistory -> {}
            }
        }
    }

    AdaptiveFilterDrawer(
        isExpandedWidth = isExpandedWidth,
        drawerContent = {
            searchFilter(
                filter = filterState.filterBundle,
                onFilterChange = {
                    filterViewModel.event(
                        SearchFilterEvent.ChangeFilters(filter = it)
                    )
                },
            )
        },
        showApplyButton = isNeedApplyFilter,
        drawerState = drawerState,
        onClose = { coroutineScope.launch { drawerState.close() } },
        onApply = {
            filterViewModel.event(SearchFilterEvent.Apply)
            coroutineScope.launch { drawerState.close() }
        },
        onTypeChanged = {
            showFilterButton = it == AdaptiveFilterDrawerType.Modal
        },
    ) {
        Scaffold(
            topBar = {
                SearchTopBar(
                    state = state,
                    historyList = searchHistoryList,
                    onQueryChanged = {
                        viewModel.event(SearchEvent.ChangeQuery(it))
                    },
                    onSearch = {
                        if (isNeedApplyFilter) {
                            filterViewModel.event(SearchFilterEvent.Apply)
                        }
                        viewModel.event(SearchEvent.Search)
                        searchHistoryViewModel.event(SearchHistoryEvent.AddToHistory(it))
                    },
                    onFilterButtonClick = { coroutineScope.launch { drawerState.fling() } },
                    onRemoveFromHistory = {
                        searchHistoryViewModel.event(SearchHistoryEvent.DeleteFromHistory(it))
                    },
                    showFilterButton = showFilterButton,
                    isExpandedWidth = isExpandedWidth,
                    scrollBehavior = scrollBehavior,
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

            SearchResultList(
                searchList = searchList,
                contentPadding = newContentPadding,
                isExpandedWidth = isExpandedWidth,
                toMediatorError = viewModel::toMediatorError,
                onSwitchFavorite = { entityId, entityType ->
                    favoritesViewModel.event(
                        FavoritesEvent.SwitchFavorite(
                            entityId = entityId,
                            entityType = entityType,
                        )
                    )
                },
                onLoadPage = onLoadPage,
                onReport = { viewModel.event(SearchEvent.ErrorReport(it)) },
            )
        }
    }
}

@Composable
private fun SearchResultList(
    searchList: LazyPagingItems<SearchItem>,
    contentPadding: PaddingValues,
    isExpandedWidth: Boolean,
    toMediatorError: (LoadState.Error) -> SearchSource.Error?,
    onSwitchFavorite: (entityId: Int, entityType: FavoriteInfo.EntityType) -> Unit,
    onLoadPage: (SearchPage) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    PagingVerticalCardGrid(
        cellSize = CardCellSize.Fixed(if (isExpandedWidth) 2 else 1),
        state = searchList.rememberLazyGridState(),
        loadState = searchList.loadState,
        isEmpty = searchList.itemCount == 0,
        placeholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_search_24,
                label = stringResource(R.string.no_search_results),
            )
        },
        contentPadding = contentPadding,
        onRefresh = { searchList.refresh() },
        onError = { state, fullscreen ->
            SearchErrorView(
                state = state,
                toMediatorError = toMediatorError,
                formatFetchErrorMessage = {
                    context.getString(R.string.fetch_search_result_error_template, it)
                },
                onRetry = { searchList.retry() },
                onReport = onReport,
                compact = !fullscreen,
            )
        },
        modifier = modifier,
    ) {
        val cardModifier = Modifier.then(
            if (isExpandedWidth) {
                Modifier.height(200.dp)
            } else {
                Modifier
            }
        )
        items(
            count = searchList.itemCount,
            key = { index -> searchList[index]?.id ?: index },
        ) { index ->
            searchList[index]?.let {
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
                when (val info = it.info) {
                    is SearchInfo.Character -> SearchItemCard(
                        characterInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Character)
                        },
                        onClick = { onLoadPage(SearchPage.Character(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Concept -> SearchItemCard(
                        conceptInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Concept)
                        },
                        onClick = { onLoadPage(SearchPage.Concept(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Episode -> SearchItemCard(
                        episodeInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            // TODO: add favorites support
                        },
                        onClick = { onLoadPage(SearchPage.Episode(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Issue -> SearchItemCard(
                        issueInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Issue)
                        },
                        onClick = { onLoadPage(SearchPage.Issue(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Location -> SearchItemCard(
                        locationInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Location)
                        },
                        onClick = { onLoadPage(SearchPage.Location(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Object -> SearchItemCard(
                        objectInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Object)
                        },
                        onClick = { onLoadPage(SearchPage.Object(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Person -> SearchItemCard(
                        personInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Person)
                        },
                        onClick = { onLoadPage(SearchPage.Person(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Series -> SearchItemCard(
                        seriesInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            // TODO: add favorites support
                        },
                        onClick = { onLoadPage(SearchPage.Series(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.StoryArc -> SearchItemCard(
                        storyArcInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.StoryArc)
                        },
                        onClick = { onLoadPage(SearchPage.StoryArc(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Team -> SearchItemCard(
                        teamInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Team)
                        },
                        onClick = { onLoadPage(SearchPage.Team(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Video -> SearchItemCard(
                        videoInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            // TODO: add favorites support
                        },
                        onClick = { onLoadPage(SearchPage.Video(info.id)) },
                        modifier = cardModifier,
                    )
                    is SearchInfo.Volume -> SearchItemCard(
                        volumeInfo = info,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onSwitchFavorite(info.id, FavoriteInfo.EntityType.Volume)
                        },
                        onClick = { onLoadPage(SearchPage.Volume(info.id)) },
                        modifier = cardModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTopBar(
    state: SearchState,
    historyList: SearchHistoryRepository.Result<List<SearchHistoryInfo>>,
    onQueryChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFilterButtonClick: () -> Unit,
    onRemoveFromHistory: (SearchHistoryInfo) -> Unit,
    scrollBehavior: SearchTopAppBarScrollBehavior,
    showFilterButton: Boolean,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(true) }

    val onSearchImpl = { q: String ->
        onSearch(q)
        isExpanded = false
    }

    SearchView(
        query = state.query,
        onQueryChanged = onQueryChanged,
        isExpanded = isExpanded,
        isSearchSubmitted = state is SearchState.Submitted,
        onExpandStateChanged = { isExpanded = it },
        onSearch = onSearchImpl,
        scrollBehavior = scrollBehavior,
        actions = {
            if (showFilterButton) {
                SearchFilterAction(
                    onClick = onFilterButtonClick,
                )
            }
        },
        history = {
            SearchHistory(
                historyList = historyList,
                onClick = {
                    onQueryChanged(it.query)
                    onSearchImpl(it.query)
                },
                onRemove = onRemoveFromHistory,
            )
        },
        isExpandedWidth = isExpandedWidth,
        modifier = modifier,
    )
}
