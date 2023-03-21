package org.proninyaroslav.opencomicvine.ui.wiki.category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.util.Pair
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesFilterBundle
import org.proninyaroslav.opencomicvine.ui.components.DateRangePickerDialog
import org.proninyaroslav.opencomicvine.ui.components.card.VolumeCard
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.viewmodel.*
import org.proninyaroslav.opencomicvine.ui.wiki.WikiPage
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.*
import java.util.*

@Composable
fun VolumesPage(
    viewModel: WikiCategoryPageViewModel,
    networkConnection: NetworkConnectionViewModel,
    datePickerViewModel: DatePickerViewModel,
    filterViewModel: VolumesFilterViewModel,
    favoritesViewModel: FavoritesViewModel,
    onLoadPage: (WikiPage) -> Unit,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val volumes = viewModel.volumesList.collectAsLazyPagingItems()
    val filterState by filterViewModel.state.collectAsStateWithLifecycle()
    val showApplyButton by remember(filterState) {
        derivedStateOf {
            when (val s = filterState) {
                is VolumesFilterState.SortChanged -> s.isNeedApply
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
                    val filter =
                        handleDatePickerResult(
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

    WikiCategoryPage(
        type = WikiCategoryPageType.Volumes,
        title = { Text(stringResource(R.string.volumes)) },
        itemCard = { item ->
            VolumeCard(
                volumeInfo = item.info,
                onClick = { onLoadPage(WikiPage.Volume(item.id)) },
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
                sort = filterState.sort,
                filterBundle = filterState.filterBundle,
                onSortChanged = {
                    filterViewModel.event(
                        VolumesFilterEvent.ChangeSort(sort = it)
                    )
                },
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
        errorMessageTemplates = WikiCategoryPage.ErrorMessageTemplates(
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
): PrefWikiVolumesFilterBundle? =
    when (type) {
        VolumesDatePickerType.Unknown -> null
        VolumesDatePickerType.DateAdded -> filterState.filterBundle.copy(
            dateAdded = PrefWikiVolumesFilter.DateAdded.InRange(
                start = Date(selection.first),
                end = Date(selection.second),
            ),
        )
        VolumesDatePickerType.DateLastUpdated -> filterState.filterBundle.copy(
            dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.InRange(
                start = Date(selection.first),
                end = Date(selection.second),
            ),
        )
    }