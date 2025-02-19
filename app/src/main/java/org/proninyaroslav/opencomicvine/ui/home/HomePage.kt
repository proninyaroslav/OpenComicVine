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

package org.proninyaroslav.opencomicvine.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoriesList
import org.proninyaroslav.opencomicvine.ui.components.error.NetworkNotAvailableSlider
import org.proninyaroslav.opencomicvine.ui.home.category.*
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkState

sealed interface HomePage {
    data object Settings : HomePage
    data object RecentCharacters : HomePage
    data object RecentIssues : HomePage
    data object RecentVolumes : HomePage
    data class Character(val characterId: Int) : HomePage
    data class Issue(val issueId: Int) : HomePage
    data class Volume(val volumeId: Int) : HomePage
    data object About : HomePage
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: HomeViewModel,
    networkConnection: NetworkConnectionViewModel,
    isExpandedWidth: Boolean,
    onLoadPage: (HomePage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val networkState by networkConnection.state.collectAsStateWithLifecycle()
    val characters = viewModel.miniCharactersList.collectAsLazyPagingItems()
    val issues = viewModel.miniIssuesList.collectAsLazyPagingItems()
    val volumes = viewModel.miniVolumesList.collectAsLazyPagingItems()
    val entities by remember(characters, issues, volumes) {
        derivedStateOf {
            listOf(characters, issues, volumes)
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    val showNetworkUnavailable by remember(networkState, entities) {
        derivedStateOf {
            networkState is NetworkState.NoConnection && entities.any { it.itemCount > 0 }
        }
    }

    LaunchedEffect(networkState, entities) {
        if (networkState is NetworkState.Reestablished) {
            entities.onEach { it.retry() }
        }
    }

    Scaffold(
        topBar = {
            HomeAppBar(
                onLoadPage = onLoadPage,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val direction = LocalLayoutDirection.current
        val newContentPadding by remember(contentPadding, isExpandedWidth) {
            derivedStateOf {
                contentPadding.removeBottomPadding(
                    direction = direction,
                    extraHorizontal = if (isExpandedWidth) 16.dp else 0.dp,
                    extraVertical = 16.dp,
                )
            }
        }

        CategoriesList(
            isExpandedWidth = isExpandedWidth,
            contentPadding = newContentPadding,
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                NetworkNotAvailableSlider(
                    targetState = showNetworkUnavailable,
                    compact = true,
                    modifier = Modifier.padding(
                        if (isExpandedWidth) 0.dp else 16.dp
                    ),
                )
            }

            item {
                IssuesCategory(
                    issues = issues,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(HomePage.RecentIssues) },
                    fullscreen = !isExpandedWidth,
                    onIssueClick = { onLoadPage(HomePage.Issue(it)) },
                    onReport = viewModel::errorReport
                )
            }

            item {
                VolumesCategory(
                    volumes = volumes,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(HomePage.RecentVolumes) },
                    fullscreen = !isExpandedWidth,
                    onVolumeClick = { onLoadPage(HomePage.Volume(it)) },
                    onReport = viewModel::errorReport
                )
            }

            item {
                CharactersCategory(
                    characters = characters,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(HomePage.RecentCharacters) },
                    fullscreen = !isExpandedWidth,
                    onCharacterClicked = { onLoadPage(HomePage.Character(it)) },
                    onReport = viewModel::errorReport
                )
            }
        }
    }
}
