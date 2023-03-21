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
import org.proninyaroslav.opencomicvine.data.item.favorites.FavoritesTeamItem
import org.proninyaroslav.opencomicvine.model.paging.favorites.FavoritesEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.TeamCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoriteItem
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun TeamsCategory(
    teams: LazyPagingItems<FavoritesTeamItem>,
    toMediatorError: (LoadState.Error) -> FavoritesEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onTeamClicked: (teamId: Int) -> Unit,
    onFavoriteClicked: (teamId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_groups_24,
                label = stringResource(R.string.teams),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = teams.rememberLazyListState(),
            loadState = teams.loadState,
            isEmpty = teams.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_groups_24,
                    label = stringResource(R.string.no_teams),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    TeamCard(
                        teamInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                FavoritesErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_teams_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_teams_list_error_template, it)
                    },
                    onRetry = { teams.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = teams.itemCount,
                key = { index -> teams[index]?.id ?: index },
            ) { index ->
                teams[index]?.let {
                    FavoriteItem(
                        onFavoriteClick = { onFavoriteClicked(it.id) },
                    ) {
                        TeamCard(
                            teamInfo = it.info,
                            onClick = { onTeamClicked(it.id) },
                        )
                    }
                }
            }
        }
    }
}