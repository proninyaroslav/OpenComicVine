/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.ui.wiki.category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.util.Pair
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiVolumesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefWikiVolumesFilterBundle
import org.proninyaroslav.opencomicvine.ui.components.DateRangePickerDialog
import org.proninyaroslav.opencomicvine.ui.components.card.VolumeCard
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.viewmodel.DatePickerState
import org.proninyaroslav.opencomicvine.ui.viewmodel.DatePickerViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.FavoritesViewModel
import org.proninyaroslav.opencomicvine.ui.viewmodel.NetworkConnectionViewModel
import org.proninyaroslav.opencomicvine.ui.wiki.WikiPage
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.VolumesDatePickerType
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.VolumesFilterState
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.VolumesFilterViewModel
import org.proninyaroslav.opencomicvine.ui.wiki.category.filter.volumesFilter
import java.util.Date

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

    LaunchedEffect(filterState, volumes) {
        if (filterState is VolumesFilterState.Applied) {
            volumes.refresh()
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
                        filterViewModel.changeFilters(
                            filterBundle = filter,
                        )
                    }
                }
            }
        },
        onHide = datePickerViewModel::hide,
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
                    filterViewModel.changeSort(sort = it)
                },
                onFiltersChanged = {
                    filterViewModel.changeFilters(filterBundle = it)
                },
                onDatePickerDialogShow = { type, date ->
                    datePickerViewModel.show(
                        dialogType = type,
                        range = date.run { Pair(first.time, second.time) }
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
        onApplyFilter = filterViewModel::apply,
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
