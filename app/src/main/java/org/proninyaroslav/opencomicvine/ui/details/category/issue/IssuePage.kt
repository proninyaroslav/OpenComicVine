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

package org.proninyaroslav.opencomicvine.ui.details.category.issue

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsCategoryPage
import org.proninyaroslav.opencomicvine.ui.details.category.DetailsState
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

private const val TAG = "IssuePage"

@Composable
fun IssuePage(
    issueId: Int,
    viewModel: IssueViewModel,
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

    LaunchedEffect(viewModel) {
        viewModel.load(issueId)
    }
    LaunchedEffect(state) {
        val s = state
        if (s is DetailsState.CacheLoadFailed) {
            Log.e(
                TAG,
                "Unable to load issue info from cache, id = $issueId",
                s.exception
            )
        }
    }

    val item = when (val s = state) {
        is DetailsState.CacheLoaded -> s.details
        is DetailsState.Loaded -> s.details
        is DetailsState.LoadFailed -> s.details
        else -> null
    }
    val otherInfoState by remember(state) {
        mutableStateOf(
            when (val s = state) {
                is DetailsState.Loaded -> s.relatedEntities.run {
                    IssueOtherInfoState(
                        creators = creators,
                        characters = characters,
                        characterDiedIn = characterDiedIn,
                        teams = teams,
                        disbandedTeams = disbandedTeams,
                        locations = locations,
                        concepts = concepts,
                        objects = objects,
                        storyArcs = storyArcs,
                    )
                }

                else -> null
            }
        )
    }

    val details = item?.details
    DetailsCategoryPage(
        type = DetailsCategoryPage.Type.Issues(issueId),
        title = details?.run {
            stringResource(
                R.string.issue_title_template_without_name,
                details.volume.name,
                details.issueNumber
            )
        },
        subtitle = details?.name,
        image = details?.image,
        fullDescription = item?.details?.description,
        state = state,
        errorMessageTemplates = DetailsCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_issue_error_template,
        ),
        shortDescriptionHeader = { isFullLoaded ->
            IssueDescriptionHeader(
                details = item?.details,
                isExpandedWidth = isExpandedWidth,
                isFullLoaded = isFullLoaded,
                onImageClick = { onLoadPage(DetailsPage.ImageViewer(it)) },
            )
        },
        generalInfo = {
            IssueGeneralInfo(
                details = item?.details,
                onLoadPage = onLoadPage,
            )
        },
        otherInfo = {
            IssueOtherInfo(
                state = otherInfoState,
                toSourceError = viewModel::toSourceError,
                onLoadPage = onLoadPage,
                onFavoriteClick = { entityId, entityType ->
                    favoritesViewModel.switchFavorite(
                        entityId = entityId,
                        entityType = entityType,
                    )
                },
                onReport = viewModel::errorReport,
            )
        },
        networkConnection = networkConnection,
        favoritesViewModel = favoritesViewModel,
        isExpandedWidth = isExpandedWidth,
        onRefresh = { viewModel.load(issueId) },
        onBackPressed = onBackPressed,
        onOpenLink = onOpenLink,
        onShareLink = onShareLink,
        onReport = viewModel::errorReport,
        modifier = modifier,
    ) { loading ->
        IssueAssociatedImages(
            images = details?.associatedImages,
            loading = loading,
            isExpandedWidth = isExpandedWidth,
            onClick = { url -> onLoadPage(DetailsPage.ImageViewer(url)) },
        )
    }
}
