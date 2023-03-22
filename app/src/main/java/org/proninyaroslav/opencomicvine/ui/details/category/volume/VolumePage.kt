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

package org.proninyaroslav.opencomicvine.ui.details.category.volume

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.FavoriteInfo
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsCategoryPage
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsEffect
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsEvent
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsState
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesEvent
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

private const val TAG = "VolumePage"

@Composable
fun VolumePage(
    volumeId: Int,
    viewModel: VolumeViewModel,
    filterViewModel: VolumeIssuesFilterViewModel,
    networkConnection: NetworkConnectionViewModel,
    favoritesViewModel: FavoritesViewModel,
    isExpandedWidth: Boolean,
    onBackPressed: () -> Unit,
    onLoadPage: (DetailsPage) -> Unit,
    onOpenLink: (Uri) -> Unit,
    onShareLink: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filterState by filterViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event(DetailsEvent.Load(volumeId))
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailsEffect.CacheLoadFailed -> {
                    Log.e(
                        TAG,
                        "Unable to load volume info from cache, id = $volumeId",
                        effect.exception
                    )
                }
            }
        }
    }

    val item = when (val s = state) {
        is DetailsState.CacheLoaded -> s.details
        is DetailsState.Loaded -> s.details
        is DetailsState.LoadFailed -> s.details
        else -> null
    }
    val issues = when (val s = state) {
        is DetailsState.Loaded -> s.relatedEntities.issues.collectAsLazyPagingItems()
        else -> null
    }
    val otherInfoState by remember(state) {
        mutableStateOf(
            when (val s = state) {
                is DetailsState.Loaded -> s.relatedEntities.run {
                    VolumeOtherInfoState(
                        characters = characters,
                        creators = creators,
                        locations = locations,
                        concepts = concepts,
                        objects = objects,
                    )
                }
                else -> null
            }
        )
    }

    LaunchedEffect(issues, filterViewModel) {
        filterViewModel.effect.collect { effect ->
            when (effect) {
                VolumeIssuesFilterEffect.Applied -> issues?.refresh()
            }
        }
    }

    val details = item?.details
    DetailsCategoryPage(
        type = DetailsCategoryPage.Type.Volumes(volumeId),
        title = details?.name,
        subtitle = details?.countOfIssues?.let {
            pluralStringResource(
                R.plurals.details_volume_count_of_issues_template,
                it,
                it,
            )
        },
        image = details?.image,
        fullDescription = details?.description,
        state = state,
        errorMessageTemplates = DetailsCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_volume_error_template,
        ),
        shortDescriptionHeader = { isFullLoaded ->
            VolumeDescriptionHeader(
                details = details,
                isExpandedWidth = isExpandedWidth,
                isFullLoaded = isFullLoaded,
                onImageClick = { onLoadPage(DetailsPage.ImageViewer(it)) },
            )
        },
        generalInfo = {
            VolumeGeneralInfo(
                details = details,
                onLoadPage = onLoadPage,
            )
        },
        otherInfo = {
            VolumeOtherInfo(
                state = otherInfoState,
                toSourceError = viewModel::toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = { entityId, entityType ->
                    favoritesViewModel.event(
                        FavoritesEvent.SwitchFavorite(
                            entityId = entityId,
                            entityType = entityType,
                        )
                    )
                },
                onReport = { viewModel.event(DetailsEvent.ErrorReport(it)) },
            )
        },
        networkConnection = networkConnection,
        favoritesViewModel = favoritesViewModel,
        isExpandedWidth = isExpandedWidth,
        onRefresh = { viewModel.event(DetailsEvent.Load(volumeId)) },
        onBackPressed = onBackPressed,
        onOpenLink = onOpenLink,
        onShareLink = onShareLink,
        onReport = { viewModel.event(DetailsEvent.ErrorReport(it)) },
        modifier = modifier,
    ) { _ ->
        IssuesList(
            issues = issues,
            header = {
                val totalIssuesCount = details?.countOfIssues ?: 0
                if (totalIssuesCount != 0) {
                    IssuesListHeader(
                        currentSort = filterState.sort,
                        issuesCount = totalIssuesCount,
                        onSortChanged = {
                            filterViewModel.event(
                                VolumeIssuesFilterEvent.ChangeSort(it)
                            )
                        },
                    )
                }
            },
            isExpandedWidth = isExpandedWidth,
            toSourceError = viewModel::toSourceError,
            onLoadPage = onLoadPage,
            onFavoriteClick = {
                favoritesViewModel.event(
                    FavoritesEvent.SwitchFavorite(
                        entityId = it,
                        entityType = FavoriteInfo.EntityType.Issue,
                    )
                )
            },
            onReport = { viewModel.event(DetailsEvent.ErrorReport(it)) },
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}
