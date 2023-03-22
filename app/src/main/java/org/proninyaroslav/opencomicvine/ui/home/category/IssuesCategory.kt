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

package org.proninyaroslav.opencomicvine.ui.home.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.item.IssueItem
import org.proninyaroslav.opencomicvine.model.paging.recent.RecentEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.IssueCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.home.RecentErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun IssuesCategory(
    issues: LazyPagingItems<IssueItem>,
    toMediatorError: (LoadState.Error) -> RecentEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onIssueClick: (issueId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_menu_book_24,
                label = stringResource(R.string.recent_issues),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = issues.rememberLazyListState(),
            loadState = issues.loadState,
            isEmpty = issues.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_menu_book_24,
                    label = stringResource(R.string.no_issues),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    IssueCard(
                        issueInfo = null,
                        compact = true,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                RecentErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_issues_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_issues_list_error_template, it)
                    },
                    onRetry = { issues.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center),
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = issues.itemCount,
                key = { index -> issues[index]?.info?.id ?: index },
            ) { index ->
                issues[index]?.info?.let {
                    IssueCard(
                        issueInfo = it,
                        compact = true,
                        onClick = { onIssueClick(it.id) },
                    )
                }
            }
        }
    }
}
