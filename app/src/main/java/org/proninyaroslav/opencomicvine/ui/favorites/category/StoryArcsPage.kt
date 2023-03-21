package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.card.StoryArcCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.FavoritesFilterViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

@Composable
fun StoryArcsPage(
    viewModel: FavoriteCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    filterViewModel: FavoritesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (FavoritesPage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoriteCategoryPage(
        type = FavoriteCategoryPageType.StoryArcs,
        title = { Text(stringResource(R.string.story_arcs)) },
        itemCard = { item ->
            StoryArcCard(
                storyArcInfo = item.info,
                onClick = { onLoadPage(FavoritesPage.StoryArc(item.id)) }
            )
        },
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_story_arc_24,
                label = stringResource(R.string.no_story_arcs),
            )
        },
        items = viewModel.storyArcsList.collectAsLazyPagingItems(),
        errorMessageTemplates = FavoriteCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_story_arcs_list_error_template,
            saveTemplate = R.string.cache_story_arcs_list_error_template,
        ),
        viewModel = viewModel,
        networkConnection = networkConnection,
        filterViewModel = filterViewModel,
        favoritesViewModel = favoritesViewModel,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}