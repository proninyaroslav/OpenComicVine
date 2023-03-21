package org.proninyaroslav.opencomicvine.ui.wiki

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.ui.components.categories.CategoriesList
import org.proninyaroslav.opencomicvine.ui.components.error.NetworkNotAvailableSlider
import org.proninyaroslav.opencomicvine.ui.removeBottomPadding
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkEffect
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkState
import org.proninyaroslav.opencomicvine.ui.wiki.category.*

sealed interface WikiPage {
    object Characters : WikiPage
    object Issues : WikiPage
    object Volumes : WikiPage
    data class Character(val characterId: Int) : WikiPage
    data class Issue(val issueId: Int) : WikiPage
    data class Volume(val volumeId: Int) : WikiPage
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiPage(
    viewModel: WikiViewModel,
    networkConnection: NetworkConnectionViewModel,
    isExpandedWidth: Boolean,
    onLoadPage: (WikiPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val networkState by networkConnection.state.collectAsStateWithLifecycle()
    val characters = viewModel.miniCharactersList.collectAsLazyPagingItems()
    val issues = viewModel.miniIssuesList.collectAsLazyPagingItems()
    val volumes = viewModel.miniVolumesList.collectAsLazyPagingItems()
    val entities by remember(characters, issues, volumes) {
        derivedStateOf {
            listOf(characters, issues, volumes)
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val showNetworkUnavailable by remember(networkState, entities) {
        derivedStateOf {
            networkState is NetworkState.NoConnection && entities.any { it.itemCount > 0 }
        }
    }

    LaunchedEffect(networkConnection) {
        networkConnection.effect.collect { effect ->
            when (effect) {
                NetworkEffect.Reestablished -> entities.onEach { it.retry() }
            }
        }
    }

    Scaffold(
        topBar = {
            WikiAppBar(scrollBehavior = scrollBehavior)
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val direction = LocalLayoutDirection.current
        val newContentPadding by remember(contentPadding, isExpandedWidth) {
            derivedStateOf {
                contentPadding.removeBottomPadding(
                    direction = direction,
                    extraHorizontal = if (isExpandedWidth) 16.dp else 0.dp,
                    extraVertical = 16.dp,
                )
            }
        }

        CategoriesList(
            isExpandedWidth = isExpandedWidth,
            contentPadding = newContentPadding,
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                NetworkNotAvailableSlider(
                    targetState = showNetworkUnavailable,
                    compact = true,
                    modifier = Modifier.padding(
                        if (isExpandedWidth) 0.dp else 16.dp
                    ),
                )
            }

            item {
                CharactersCategory(
                    characters = characters,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(WikiPage.Characters) },
                    fullscreen = !isExpandedWidth,
                    onCharacterClicked = { onLoadPage(WikiPage.Character(it)) },
                    onReport = { viewModel.event(WikiEvent.ErrorReport(it)) },
                )
            }

            item {
                IssuesCategory(
                    issues = issues,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(WikiPage.Issues) },
                    fullscreen = !isExpandedWidth,
                    onIssueClick = { onLoadPage(WikiPage.Issue(it)) },
                    onReport = { viewModel.event(WikiEvent.ErrorReport(it)) },
                )
            }

            item {
                VolumesCategory(
                    volumes = volumes,
                    toMediatorError = viewModel::toMediatorError,
                    onClick = { onLoadPage(WikiPage.Volumes) },
                    fullscreen = !isExpandedWidth,
                    onVolumeClick = { onLoadPage(WikiPage.Volume(it)) },
                    onReport = { viewModel.event(WikiEvent.ErrorReport(it)) },
                )
            }
        }
    }
}