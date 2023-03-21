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
import org.proninyaroslav.opencomicvine.data.item.CharacterItem
import org.proninyaroslav.opencomicvine.model.paging.recent.RecentEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryHeader
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryPagingRow
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoryView
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.home.RecentErrorView
import org.proninyaroslav.opencomicvine.ui.rememberLazyListState

@Composable
fun CharactersCategory(
    characters: LazyPagingItems<CharacterItem>,
    toMediatorError: (LoadState.Error) -> RecentEntityRemoteMediator.Error?,
    fullscreen: Boolean,
    onClick: () -> Unit,
    onCharacterClicked: (characterId: Int) -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    CategoryView(
        header = {
            CategoryHeader(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.recent_characters),
                onClick = onClick,
            )
        },
        modifier = modifier,
        fullscreen = fullscreen,
    ) {
        CategoryPagingRow(
            state = characters.rememberLazyListState(),
            loadState = characters.loadState,
            isEmpty = characters.itemCount == 0,
            placeholder = {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_face_24,
                    label = stringResource(R.string.no_characters),
                    compact = fullscreen,
                )
            },
            loadingPlaceholder = {
                repeat(3) {
                    CharacterCard(
                        characterInfo = null,
                        onClick = {},
                    )
                }
            },
            onError = { state ->
                RecentErrorView(
                    state = state,
                    toMediatorError = toMediatorError,
                    formatFetchErrorMessage = {
                        context.getString(R.string.fetch_characters_list_error_template, it)
                    },
                    formatSaveErrorMessage = {
                        context.getString(R.string.cache_characters_list_error_template, it)
                    },
                    onRetry = { characters.retry() },
                    onReport = onReport,
                    compact = true,
                    modifier = Modifier.align(Alignment.Center),
                )
            },
            onLoadMore = onClick,
        ) {
            items(
                count = characters.itemCount,
                key = { index -> characters[index]?.id ?: index },
            ) { index ->
                characters[index]?.let {
                    CharacterCard(
                        characterInfo = it.info,
                        onClick = { onCharacterClicked(it.id) },
                    )
                }
            }
        }
    }
}