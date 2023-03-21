package org.proninyaroslav.opencomicvine.ui.details.category.volume

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.item.IssueItem
import org.proninyaroslav.opencomicvine.model.paging.details.DetailsEntitySource
import org.proninyaroslav.opencomicvine.model.repo.FavoriteFetchResult
import org.proninyaroslav.opencomicvine.ui.components.FavoriteBox
import org.proninyaroslav.opencomicvine.ui.components.FavoriteFilledTonalButton
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.components.list.PagingCardRow
import org.proninyaroslav.opencomicvine.ui.details.DetailsErrorView
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

private const val TAG = "VolumeIssuesList"

@Composable
fun IssuesList(
    issues: LazyPagingItems<IssueItem>?,
    header: @Composable () -> Unit,
    isExpandedWidth: Boolean,
    toSourceError: (LoadState.Error) -> DetailsEntitySource.Error?,
    onLoadPage: (DetailsPage) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    OutlinedCard(
        modifier = modifier,
    ) {
        if (issues == null) {
            LoadingPlaceholder(isExpandedWidth = isExpandedWidth)
        } else {
            header()
            PagingCardRow(
                state = issues.rememberLazyListState(),
                loadState = issues.loadState,
                isEmpty = issues.itemCount == 0,
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                placeholder = {
                    EmptyListPlaceholder(
                        icon = R.drawable.ic_menu_book_24,
                        label = stringResource(R.string.no_issues),
                        compact = true,
                    )
                },
                loadingPlaceholder = { LoadingPlaceholder(isExpandedWidth = isExpandedWidth) },
                onError = { state ->
                    DetailsErrorView(
                        state = state,
                        toSourceError = toSourceError,
                        formatFetchErrorMessage = {
                            context.getString(R.string.fetch_issues_list_error_template, it)
                        },
                        compact = true,
                        onRefresh = { issues.retry() },
                        onReport = onReport,
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .align(Alignment.Center)
                            .padding(16.dp),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (isExpandedWidth) {
                            VolumeIssueCardExpandedWidth
                        } else {
                            VolumeIssueCardWidth
                        } * 2.3f
                    ),
            ) {
                items(issues, key = { it.id }) { issue ->
                    issue?.let {
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
                        FavoriteBox(
                            iconAlignment = Alignment.TopStart,
                            icon = {
                                FavoriteFilledTonalButton(
                                    isFavorite = isFavorite,
                                    onClick = { onFavoriteClick(it.id) },
                                    modifier = Modifier.offset(8.dp, 8.dp)
                                )
                            },
                        ) {
                            VolumeIssueCard(
                                issueInfo = issue.info,
                                isExpandedWidth = isExpandedWidth,
                                onClick = { onLoadPage(DetailsPage.Issue(issue.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder(isExpandedWidth: Boolean) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        repeat(3) {
            VolumeIssueCard(
                issueInfo = null,
                isExpandedWidth = isExpandedWidth,
                onClick = {},
            )
        }
    }
}