package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.card.CharacterCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.FavoritesFilterViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

@Composable
fun CharactersPage(
    viewModel: FavoriteCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    filterViewModel: FavoritesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (FavoritesPage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoriteCategoryPage(
        type = FavoriteCategoryPageType.Characters,
        title = { Text(stringResource(R.string.characters)) },
        itemCard = { item ->
            CharacterCard(
                characterInfo = item.info,
                onClick = { onLoadPage(FavoritesPage.Character(item.id)) }
            )
        },
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.no_characters),
            )
        },
        items = viewModel.charactersList.collectAsLazyPagingItems(),
        errorMessageTemplates = FavoriteCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_characters_list_error_template,
            saveTemplate = R.string.cache_characters_list_error_template,
        ),
        viewModel = viewModel,
        networkConnection = networkConnection,
        filterViewModel = filterViewModel,
        favoritesViewModel = favoritesViewModel,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}