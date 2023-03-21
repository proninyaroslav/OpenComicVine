package org.proninyaroslav.opencomicvine.ui.components.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.list.PagingCardRow
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CategoryView(
    header: @Composable () -> Unit,
    fullscreen: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (fullscreen) {
        Body(
            header = header,
            fullscreen = true,
            content = content,
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        OutlinedCard(
            modifier = modifier.aspectRatio(1.2f),
        ) {
            Body(
                header = header,
                fullscreen = false,
                content = content,
            )
        }
    }
}

@Composable
private fun Body(
    header: @Composable () -> Unit,
    fullscreen: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val mod = if (!fullscreen) modifier.padding(vertical = 16.dp) else modifier
    Column(
        modifier = mod,
    ) {
        header()
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = if (!fullscreen) {
                Modifier.fillMaxHeight()
            } else {
                Modifier
            },
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Preview("Fullscreen")
@Composable
fun PreviewCategoryView_Fulscreen() {
    OpenComicVineTheme {
        CategoryView(
            header = {
                CategoryHeader(
                    icon = R.drawable.ic_face_24,
                    label = stringResource(R.string.characters),
                    onClick = {},
                )
            },
            fullscreen = true,
        ) {
            PagingCardRow(
                state = rememberLazyListState(),
                loadState = CombinedLoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                    source = LoadStates(
                        refresh = LoadState.NotLoading(false),
                        append = LoadState.NotLoading(false),
                        prepend = LoadState.NotLoading(false),
                    ),
                ),
                placeholder = {},
                loadingPlaceholder = {},
                onError = {},
                isEmpty = false,
            ) {
                items(10) {
                    Card(modifier = Modifier.size(150.dp, 200.dp)) {}
                }
            }
        }
    }
}

@Preview("Compact")
@Composable
fun PreviewCategoryView_Compact() {
    OpenComicVineTheme {
        CategoryView(
            fullscreen = false,
            header = {
                CategoryHeader(
                    icon = R.drawable.ic_face_24,
                    label = stringResource(R.string.characters),
                    onClick = {},
                )
            },
        ) {
            PagingCardRow(
                state = rememberLazyListState(),
                loadState = CombinedLoadStates(
                    refresh = LoadState.NotLoading(false),
                    append = LoadState.NotLoading(false),
                    prepend = LoadState.NotLoading(false),
                    source = LoadStates(
                        refresh = LoadState.NotLoading(false),
                        append = LoadState.NotLoading(false),
                        prepend = LoadState.NotLoading(false),
                    ),
                ),
                placeholder = {},
                loadingPlaceholder = {},
                onError = {},
                isEmpty = false,
            ) {
                items(10) {
                    Card(modifier = Modifier.size(150.dp, 200.dp)) {}
                }
            }
        }
    }
}