package org.proninyaroslav.opencomicvine.ui.home.category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.util.Pair
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentVolumesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentVolumesFilterBundle
import org.proninyaroslav.opencomicvine.ui.components.DateRangePickerDialog
import org.proninyaroslav.opencomicvine.ui.components.card.VolumeCard
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.home.HomePage
import org.proninyaroslav.opencomicvine.ui.home.category.filter.*
import org.proninyaroslav.opencomicvine.ui.viewmodel.*
import java.util.*

@Composable
fun VolumesPage(
    viewModel: RecentCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    datePickerViewModel: DatePickerViewModel,
    filterViewModel: VolumesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (HomePage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val volumes = viewModel.volumesList.collectAsLazyPagingItems()
    val filterState by filterViewModel.state.collectAsStateWithLifecycle()
    val showApplyButton by remember(filterState) {
        derivedStateOf {
            when (val s = filterState) {
                is VolumesFilterState.FiltersChanged -> s.isNeedApply
                else -> false
            }
        }
    }
    val datePickerState by datePickerViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(filterViewModel) {
        filterViewModel.effect.collect { effect ->
            when (effect) {
                VolumesFilterEffect.Applied -> volumes.refresh()
            }
        }
    }

    DateRangePickerDialog(
        show = when (datePickerState) {
            DatePickerState.Hide,
            DatePickerState.Initial -> false
            is DatePickerState.Show<*> -> true
        },
        titleText = R.string.date_picker_select_dates,
        initRange = when (val s = datePickerState) {
            DatePickerState.Hide,
            DatePickerState.Initial -> null
            is DatePickerState.Show<*> -> s.range
        },
        onPositiveClicked = { selection ->
            when (val s = datePickerState) {
                DatePickerState.Hide -> {}
                DatePickerState.Initial -> {}
                is DatePickerState.Show<*> -> {
                    val filter = handleDatePickerResult(
                        filterState = filterState,
                        type = s.dialogType as VolumesDatePickerType,
                        selection = selection,
                    )
                    filter?.let {
                        filterViewModel.event(
                            VolumesFilterEvent.ChangeFilters(
                                filterBundle = filter,
                            )
                        )
                    }
                }
            }
        },
        onHide = { datePickerViewModel.event(DatePickerEvent.Hide) },
    )

    RecentCategoryPage(
        type = RecentCategoryPageType.Volumes,
        title = { Text(stringResource(R.string.recent_volumes)) },
        itemCard = { item ->
            VolumeCard(
                volumeInfo = item.info,
                onClick = { onLoadPage(HomePage.Volume(item.id)) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        emptyListPlaceholder = {
            EmptyListPlaceholder(
                icon = R.drawable.ic_library_books_24,
                label = stringResource(R.string.no_volumes),
            )
        },
        filterDrawerContent = {
            volumesFilter(
                filterBundle = filterState.filterBundle,
                onFiltersChanged = {
                    filterViewModel.event(
                        VolumesFilterEvent.ChangeFilters(filterBundle = it)
                    )
                },
                onDatePickerDialogShow = { type, date ->
                    datePickerViewModel.event(
                        DatePickerEvent.Show(
                            dialogType = type,
                            range = date.run { Pair(first.time, second.time) }
                        )
                    )
                },
            )
        },
        items = volumes,
        errorMessageTemplates = RecentCategoryPage.ErrorMessageTemplates(
            fetchTemplate = R.string.fetch_volumes_list_error_template,
            saveTemplate = R.string.cache_volumes_list_error_template,
        ),
        cellSize = CardCellSize.Adaptive.Large,
        showApplyButton = showApplyButton,
        onApplyFilter = { filterViewModel.event(VolumesFilterEvent.Apply) },
        viewModel = viewModel,
        networkConnection = networkConnection,
        favoritesViewModel = favoritesViewModel,
        onBackButtonClicked = onBackButtonClicked,
        modifier = modifier,
    )
}

private fun handleDatePickerResult(
    filterState: VolumesFilterState,
    type: VolumesDatePickerType,
    selection: Pair<Long, Long>,
): PrefRecentVolumesFilterBundle? =
    when (type) {
        VolumesDatePickerType.Unknown -> null
        VolumesDatePickerType.DateAdded -> filterState.filterBundle.copy(
            dateAdded = PrefRecentVolumesFilter.DateAdded.InRange(
                start = Date(selection.first),
                end = Date(selection.second),
            ),
        )
    }