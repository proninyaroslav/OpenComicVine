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

package org.proninyaroslav.opencomicvine.ui.details.category

import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import org.proninyaroslav.opencomicvine.types.ImageInfo
import org.proninyaroslav.opencomicvine.types.item.BaseItem
import org.proninyaroslav.opencomicvine.model.ComicVineUrlBuilder
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.model.repo.FavoritesRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.AnimatedSlideContent
import org.proninyaroslav.opencomicvine.ui.components.FavoriteFilledTonalActionButton
import org.proninyaroslav.opencomicvine.ui.components.FilledTonalActionButton
import org.proninyaroslav.opencomicvine.ui.components.error.ComicVineResultErrorView
import org.proninyaroslav.opencomicvine.ui.components.rememberTopAppBarWithImageScrollBehavior
import org.proninyaroslav.opencomicvine.ui.details.*
import org.proninyaroslav.opencomicvine.ui.viewmodel.*

interface DetailsCategoryPage {
    sealed interface Type {
        val id: Int

        data class Characters(override val id: Int) : Type
        data class Issues(override val id: Int) : Type
        data class Volumes(override val id: Int) : Type
    }

    data class ErrorMessageTemplates(
        @StringRes val fetchTemplate: Int,
    )
}

private const val TAG = "DetailsCategoryPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <D : BaseItem> DetailsCategoryPage(
    modifier: Modifier = Modifier,
    type: DetailsCategoryPage.Type,
    title: String?,
    subtitle: String? = null,
    image: ImageInfo?,
    fullDescription: String?,
    state: DetailsState<D, DetailsViewModel.RelatedEntities>,
    errorMessageTemplates: DetailsCategoryPage.ErrorMessageTemplates,
    shortDescriptionHeader: @Composable (isFullLoaded: Boolean) -> Unit,
    generalInfo: @Composable () -> Unit,
    otherInfo: @Composable () -> Unit,
    networkConnection: NetworkConnectionViewModel,
    favoritesViewModel: FavoritesViewModel,
    isExpandedWidth: Boolean,
    onRefresh: () -> Unit,
    onBackPressed: () -> Unit,
    onOpenLink: (Uri) -> Unit,
    onShareLink: (Uri) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    additionalContent: @Composable (loading: Boolean) -> Unit = {},
) {
    val scrollBehavior = rememberTopAppBarWithImageScrollBehavior()
    var longClickLink by remember { mutableStateOf<Uri?>(null) }
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val networkState by networkConnection.state.collectAsStateWithLifecycle()
    val switchFavoriteState by favoritesViewModel.switchFavorite.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(switchFavoriteState) {
        when (val s = switchFavoriteState) {
            is SwitchFavoriteState.Failed -> when (val error = s.error) {
                is FavoritesRepository.Result.Failed.IO -> coroutineScope.launch {
                    snackbarState.showSnackbar(
                        context.getString(
                            R.string.error_add_delete_from_favorites,
                            error.exception
                        )
                    )
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(networkState, onRefresh) {
        if (networkState is NetworkState.Reestablished) {
            onRefresh()
        }
    }

    val (item, loading) = when (state) {
        is DetailsState.CacheLoaded -> state.details to true
        is DetailsState.Loaded -> state.details to false
        is DetailsState.LoadFailed -> state.details to true
        else -> null to true
    }
    val isFavorite by produceState(initialValue = false, item?.isFavorite) {
        item?.isFavorite?.collect {
            value = when (it) {
                is FavoriteFetchResult.Success -> it.isFavorite
                is FavoriteFetchResult.Failed.IO -> {
                    Log.e(TAG, "Unable to get favorites status", it.exception)
                    false
                }
            }
        }
    }

    DetailsScaffold(
        topBar = {
            ComicVineBannerAppBar(
                title = title,
                subtitle = subtitle,
                image = image,
                actions = {
                    FilledTonalActionButton(
                        onClick = { onShareLink(buildUri(type)) },
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_share_24),
                            contentDescription = stringResource(R.string.share),
                        )
                    }
                    FavoriteFilledTonalActionButton(
                        isFavorite = isFavorite,
                        onClick = {
                            favoritesViewModel.switchFavorite(
                                entityId = type.id,
                                entityType = type.toEntityType(),
                            )
                        },
                    )
                },
                isExpandedWidth = isExpandedWidth,
                onBackButtonClicked = onBackPressed,
                scrollBehavior = scrollBehavior,
            )
        },
        headerContent = {
            Column {
                AnimatedSlideContent(
                    targetState = state,
                    alignment = Alignment.Top,
                ) { state ->
                    if (state is DetailsState.LoadFailed) {
                        ComicVineResultErrorView(
                            error = state.error,
                            formatFetchErrorMessage = {
                                context.getString(errorMessageTemplates.fetchTemplate, it)
                            },
                            compact = true,
                            onRetry = onRefresh,
                            onReport = onReport,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
                shortDescriptionHeader(!loading)
            }
        },
        secondaryContent = {
            generalInfo()
            otherInfo()
        },
        contentPadding = PaddingValues(16.dp),
        isExpandedWidth = isExpandedWidth,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        additionalContent(loading)
        DescriptionView(
            description = fullDescription,
            loading = loading,
            isExpandedWidth = isExpandedWidth,
            onLinkClick = onOpenLink,
            onLinkLongClick = { longClickLink = it },
        )
    }

    if (longClickLink != null) {
        DescriptionContextMenu(
            title = longClickLink.toString(),
            onDismiss = { longClickLink = null },
            onAction = {
                when (it) {
                    DescriptionContextMenuAction.CopyLink -> {
                        clipboardManager.setText(AnnotatedString(longClickLink.toString()))
                        longClickLink = null
                        coroutineScope.launch {
                            snackbarState.showSnackbar(
                                context.getString(R.string.copied_to_clipboard),
                            )
                        }
                    }

                    DescriptionContextMenuAction.ShareLink -> {
                        onShareLink(longClickLink!!)
                        longClickLink = null
                    }
                }
            }
        )
    }
}

private fun DetailsCategoryPage.Type.toEntityType(): FavoriteInfo.EntityType = when (this) {
    is DetailsCategoryPage.Type.Characters -> FavoriteInfo.EntityType.Character
    is DetailsCategoryPage.Type.Issues -> FavoriteInfo.EntityType.Issue
    is DetailsCategoryPage.Type.Volumes -> FavoriteInfo.EntityType.Volume
}

private fun buildUri(type: DetailsCategoryPage.Type): Uri = when (type) {
    is DetailsCategoryPage.Type.Characters -> ComicVineUrlBuilder.character(type.id)
    is DetailsCategoryPage.Type.Issues -> ComicVineUrlBuilder.issue(type.id)
    is DetailsCategoryPage.Type.Volumes -> ComicVineUrlBuilder.volume(type.id)
}.toUri()
