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

package org.proninyaroslav.opencomicvine.ui.home.category.filter

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentIssuesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentIssuesFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefRecentIssuesSort
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDatePickerItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterRadioButtonItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

enum class IssuesDatePickerType {
    Unknown,
    DateAdded,
    CoverDate,
}

fun LazyListScope.issuesFilter(
    sort: PrefRecentIssuesSort,
    filterBundle: PrefRecentIssuesFilterBundle,
    onFiltersChanged: (PrefRecentIssuesFilterBundle) -> Unit,
    onSortChanged: (PrefRecentIssuesSort) -> Unit,
    onDatePickerDialogShow: (IssuesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    sort(sort, onSortChanged)

    storeDateFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)

    dateAddedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)
}

private fun LazyListScope.sort(
    sort: PrefRecentIssuesSort,
    onSortChanged: (PrefRecentIssuesSort) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.sort),
            icon = R.drawable.ic_sort_24,
        )
    }

    items(sortItems) {
        FilterRadioButtonItem(
            label = { Text(stringResource(it.label)) },
            selected = sort == it.sort,
            onClick = { onSortChanged(it.sort) }
        )
    }
}

private fun LazyListScope.storeDateFilter(
    filterBundle: PrefRecentIssuesFilterBundle,
    onFiltersChanged: (PrefRecentIssuesFilterBundle) -> Unit,
    onDatePickerDialogShow: (IssuesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_store_date),
            icon = R.drawable.ic_calendar_month_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_store_date_all)) },
            selected = filterBundle.storeDate == PrefRecentIssuesFilter.StoreDate.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        storeDate = PrefRecentIssuesFilter.StoreDate.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_store_date_next_week)) },
            selected = filterBundle.storeDate == PrefRecentIssuesFilter.StoreDate.NextWeek,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        storeDate = PrefRecentIssuesFilter.StoreDate.NextWeek,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_store_date_in_range)) },
            selected = filterBundle.storeDate is PrefRecentIssuesFilter.StoreDate.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        storeDate = getDaysOfCurrentWeek().run {
                            PrefRecentIssuesFilter.StoreDate.InRange(
                                start = first,
                                end = second,
                            )
                        },
                    )
                )
            }
        )
    }

    item {
        val enabled by remember(filterBundle) {
            derivedStateOf { filterBundle.storeDate is PrefRecentIssuesFilter.StoreDate.InRange }
        }
        FilterDatePickerItem(
            value = when (val coverDate = filterBundle.storeDate) {
                PrefRecentIssuesFilter.StoreDate.Unknown,
                PrefRecentIssuesFilter.StoreDate.NextWeek -> getDaysOfCurrentWeek()
                is PrefRecentIssuesFilter.StoreDate.InRange -> {
                    coverDate.run { Pair(start, end) }
                }
            },
            enabled = enabled,
            onClick = {
                when (val coverDate = filterBundle.storeDate) {
                    PrefRecentIssuesFilter.StoreDate.Unknown,
                    PrefRecentIssuesFilter.StoreDate.NextWeek -> throw IllegalStateException()
                    is PrefRecentIssuesFilter.StoreDate.InRange -> coverDate.run {
                        onDatePickerDialogShow(IssuesDatePickerType.CoverDate, Pair(start, end))
                    }
                }
            }
        )
    }
}

private fun LazyListScope.dateAddedFilter(
    filterBundle: PrefRecentIssuesFilterBundle,
    onFiltersChanged: (PrefRecentIssuesFilterBundle) -> Unit,
    onDatePickerDialogShow: (IssuesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_date_added),
            icon = R.drawable.ic_calendar_month_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_all)) },
            selected = filterBundle.dateAdded == PrefRecentIssuesFilter.DateAdded.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_this_week)) },
            selected = filterBundle.dateAdded == PrefRecentIssuesFilter.DateAdded.ThisWeek,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = PrefRecentIssuesFilter.DateAdded.ThisWeek,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_in_range)) },
            selected = filterBundle.dateAdded is PrefRecentIssuesFilter.DateAdded.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = getDaysOfCurrentWeek().run {
                            PrefRecentIssuesFilter.DateAdded.InRange(
                                start = first,
                                end = second,
                            )
                        },
                    )
                )
            }
        )
    }

    item {
        val enabled by remember(filterBundle) {
            derivedStateOf { filterBundle.dateAdded is PrefRecentIssuesFilter.DateAdded.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateAdded = filterBundle.dateAdded) {
                PrefRecentIssuesFilter.DateAdded.Unknown,
                PrefRecentIssuesFilter.DateAdded.ThisWeek -> getDaysOfCurrentWeek()
                is PrefRecentIssuesFilter.DateAdded.InRange -> dateAdded.run { Pair(start, end) }
            },
            enabled = enabled,
            onClick = {
                when (val dateAdded = filterBundle.dateAdded) {
                    PrefRecentIssuesFilter.DateAdded.Unknown,
                    PrefRecentIssuesFilter.DateAdded.ThisWeek -> throw IllegalStateException()
                    is PrefRecentIssuesFilter.DateAdded.InRange -> dateAdded.run {
                        onDatePickerDialogShow(IssuesDatePickerType.DateAdded, Pair(start, end))
                    }
                }
            }
        )
    }
}

private data class SortItem(
    @StringRes val label: Int,
    val sort: PrefRecentIssuesSort,
)

private val sortItems = listOf(
    SortItem(
        label = R.string.sort_store_date_desc,
        sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
    ),
    SortItem(
        label = R.string.sort_store_date_asc,
        sort = PrefRecentIssuesSort.StoreDate(
            direction = PrefSortDirection.Asc,
        )
    ),
    SortItem(
        label = R.string.sort_date_added_desc,
        sort = PrefRecentIssuesSort.DateAdded(
            direction = PrefSortDirection.Desc,
        )
    ),
    SortItem(
        label = R.string.sort_date_added_asc,
        sort = PrefRecentIssuesSort.DateAdded(
            direction = PrefSortDirection.Asc,
        )
    ),
)

@Preview(showBackground = true)
@Composable
private fun PreviewIssuesFilter() {
    var filterBundle by remember {
        mutableStateOf(
            PrefRecentIssuesFilterBundle(
                dateAdded = PrefRecentIssuesFilter.DateAdded.Unknown,
                storeDate = PrefRecentIssuesFilter.StoreDate.Unknown,
            )
        )
    }
    var sort: PrefRecentIssuesSort by remember {
        mutableStateOf(
            PrefRecentIssuesSort.StoreDate(
                direction = PrefSortDirection.Desc,
            )
        )
    }
    OpenComicVineTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            issuesFilter(
                sort = sort,
                filterBundle = filterBundle,
                onFiltersChanged = { filterBundle = it },
                onSortChanged = { sort = it },
                onDatePickerDialogShow = { _, _ -> },
            )
        }
    }
}
