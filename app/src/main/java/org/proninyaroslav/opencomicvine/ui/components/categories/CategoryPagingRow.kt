package org.proninyaroslav.opencomicvine.ui.components.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import org.proninyaroslav.opencomicvine.ui.components.list.PagingCardRow

@Composable
fun CategoryPagingRow(
    modifier: Modifier = Modifier,
    state: LazyListState,
    loadState: CombinedLoadStates,
    isEmpty: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    placeholder: @Composable () -> Unit,
    loadingPlaceholder: @Composable RowScope.() -> Unit,
    onError: @Composable BoxScope.(state: LoadState.Error) -> Unit,
    onLoadMore: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    PagingCardRow(
        state = state,
        loadState = loadState,
        isEmpty = isEmpty,
        contentPadding = contentPadding,
        placeholder = placeholder,
        loadingPlaceholder = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                loadingPlaceholder()
            }
        },
        onError = { s ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                onError(s)
            }
        },
        modifier = modifier,
    ) {
        item {
            Spacer(modifier = Modifier.width(16.dp))
        }
        content()
        item {
            LoadMoreButton(
                onClick = onLoadMore,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}