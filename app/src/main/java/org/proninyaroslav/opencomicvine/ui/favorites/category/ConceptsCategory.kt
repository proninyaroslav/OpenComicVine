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
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesConceptItem
import org.proninyaroslav.opencomicvine.model.paging.favorites.FavoritesEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.ConceptCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoriteItem
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun ConceptsCategory(
    concepts: LazyPagingItems<FavoritesConceptItem>,
    toMediatorError: (LoadState.Error) -> FavoritesEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onConceptClicked: (conceptId: Int) -> Unit,
    onFavoriteClicked: (conceptId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_menu_book_24,
                label = stringResource(R.string.concepts),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = concepts.rememberLazyListState(),
            loadState = concepts.loadState,
            isEmpty = concepts.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_menu_book_24,
                    label = stringResource(R.string.no_concepts),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    ConceptCard(
                        conceptInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                FavoritesErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_concepts_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_concepts_list_error_template, it)
                    },
                    onRetry = { concepts.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = concepts.itemCount,
                key = { index -> concepts[index]?.id ?: index },
            ) { index ->
                concepts[index]?.let {
                    FavoriteItem(
                        onFavoriteClick = { onFavoriteClicked(it.id) },
                    ) {
                        ConceptCard(
                            conceptInfo = it.info,
                            onClick = { onConceptClicked(it.id) },
                        )
                    }
                }
            }
        }
    }
}