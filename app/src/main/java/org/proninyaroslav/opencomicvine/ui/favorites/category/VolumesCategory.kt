package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesVolumeItem
import org.proninyaroslav.opencomicvine.model.paging.favorites.FavoritesEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.VolumeCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoriteItem
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun VolumesCategory(
    volumes: LazyPagingItems<FavoritesVolumeItem>,
    toMediatorError: (LoadState.Error) -> FavoritesEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onVolumeClicked: (volumeId: Int) -> Unit,
    onFavoriteClicked: (volumeId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_library_books_24,
                label = stringResource(R.string.volumes),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = volumes.rememberLazyListState(),
            loadState = volumes.loadState,
            isEmpty = volumes.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_library_books_24,
                    label = stringResource(R.string.no_volumes),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    VolumeCard(
                        volumeInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                FavoritesErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_volumes_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_volumes_list_error_template, it)
                    },
                    onRetry = { volumes.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = volumes.itemCount,
                key = { index -> volumes[index]?.id ?: index },
            ) { index ->
                volumes[index]?.let {
                    FavoriteItem(
                        onFavoriteClick = { onFavoriteClicked(it.id) },
                    ) {
                        VolumeCard(
                            volumeInfo = it.info,
                            compact = true,
                            onClick = { onVolumeClicked(it.id) },
                        )
                    }
                }
            }
        }
    }
}