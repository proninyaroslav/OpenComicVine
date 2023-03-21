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
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesMovieItem
import org.proninyaroslav.opencomicvine.model.paging.favorites.FavoritesEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.MovieCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoriteItem
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun MoviesCategory(
    movies: LazyPagingItems<FavoritesMovieItem>,
    toMediatorError: (LoadState.Error) -> FavoritesEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onMovieClicked: (movieId: Int) -> Unit,
    onFavoriteClicked: (movieId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_movie_24,
                label = stringResource(R.string.movies),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = movies.rememberLazyListState(),
            loadState = movies.loadState,
            isEmpty = movies.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_menu_book_24,
                    label = stringResource(R.string.no_movies),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    MovieCard(
                        movieInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                FavoritesErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_movies_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_movies_list_error_template, it)
                    },
                    onRetry = { movies.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = movies.itemCount,
                key = { index -> movies[index]?.id ?: index },
            ) { index ->
                movies[index]?.let {
                    FavoriteItem(
                        onFavoriteClick = { onFavoriteClicked(it.id) },
                    ) {
                        MovieCard(
                            movieInfo = it.info,
                            onClick = { onMovieClicked(it.id) },
                        )
                    }
                }
            }
        }
    }
}