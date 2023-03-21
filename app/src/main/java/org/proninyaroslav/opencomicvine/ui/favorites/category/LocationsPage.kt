package org.proninyaroslav.opencomicvine.ui.favorites.category

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.card.LocationCard
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.favorites.FavoritesPage
import org.proninyaroslav.opencomicvine.ui.favorites.category.filter.FavoritesFilterViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel

@Composable
fun LocationsPage(
    viewModel: FavoriteCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    filterViewModel: FavoritesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (FavoritesPage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoriteCategoryPage(
        type = FavoriteCategoryPageType.Locations,
        title = { Text(stringResource(R.string.locations)) },
        itemCard = { item ->
            LocationCard(
                locationInfo = item.info,
                onClick = { onLoadPage(FavoritesPage.Location(item.id)) }
            )
        },
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_public_24,
                label = stringResource(R.string.no_locations),
            )
        },
        items = viewModel.locationsList.collectAsLazyPagingItems(),
        errorMessageTemplates = FavoriteCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_locations_list_error_template,
            saveTemplate = R.string.cache_locations_list_error_template,
        ),
        viewModel = viewModel,
        networkConnection = networkConnection,
        filterViewModel = filterViewModel,
        favoritesViewModel = favoritesViewModel,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}