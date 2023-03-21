package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.card.VolumeCard
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.FavoritesFilterViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

@Composable
fun VolumesPage(
    viewModel: FavoriteCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    filterViewModel: FavoritesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (FavoritesPage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoriteCategoryPage(
        type = FavoriteCategoryPageType.Volumes,
        title = { Text(stringResource(R.string.volumes)) },
        itemCard = { item ->
            VolumeCard(
                volumeInfo = item.info,
                onClick = { onLoadPage(FavoritesPage.Volume(item.id)) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_library_books_24,
                label = stringResource(R.string.no_volumes),
            )
        },
        items = viewModel.volumesList.collectAsLazyPagingItems(),
        errorMessageTemplates = FavoriteCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_volumes_list_error_template,
            saveTemplate = R.string.cache_volumes_list_error_template,
        ),
        cellSize = CardCellSize.Adaptive.Large,
        viewModel = viewModel,
        networkConnection = networkConnection,
        filterViewModel = filterViewModel,
        favoritesViewModel = favoritesViewModel,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}