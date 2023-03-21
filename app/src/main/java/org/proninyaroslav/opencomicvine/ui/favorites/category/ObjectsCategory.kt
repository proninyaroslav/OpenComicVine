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
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesObjectItem
import org.proninyaroslav.opencomicvine.model.paging.favorites.FavoritesEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.ObjectCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoriteItem
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun ObjectsCategory(
    objects: LazyPagingItems<FavoritesObjectItem>,
    toMediatorError: (LoadState.Error) -> FavoritesEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onObjectClicked: (objectId: Int) -> Unit,
    onFavoriteClicked: (objectId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_cube_outline_24,
                label = stringResource(R.string.things),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = objects.rememberLazyListState(),
            loadState = objects.loadState,
            isEmpty = objects.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_cube_outline_24,
                    label = stringResource(R.string.no_things),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    ObjectCard(
                        objectInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                FavoritesErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_things_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_things_list_error_template, it)
                    },
                    onRetry = { objects.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = objects.itemCount,
                key = { index -> objects[index]?.id ?: index },
            ) { index ->
                objects[index]?.let {
                    FavoriteItem(
                        onFavoriteClick = { onFavoriteClicked(it.id) },
                    ) {
                        ObjectCard(
                            objectInfo = it.info,
                            onClick = { onObjectClicked(it.id) },
                        )
                    }
                }
            }
        }
    }
}