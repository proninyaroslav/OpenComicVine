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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentVolumesFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefRecentVolumesFilterBundle
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDatePickerItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterRadioButtonItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

enum class VolumesDatePickerType {
    Unknown,
    DateAdded,
}

fun LazyListScope.volumesFilter(
    filterBundle: PrefRecentVolumesFilterBundle,
    onFiltersChanged: (PrefRecentVolumesFilterBundle) -> Unit,
    onDatePickerDialogShow: (VolumesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    dateAddedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)
}

private fun LazyListScope.dateAddedFilter(
    filterBundle: PrefRecentVolumesFilterBundle,
    onFiltersChanged: (PrefRecentVolumesFilterBundle) -> Unit,
    onDatePickerDialogShow: (VolumesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_date_added),
            icon = R.drawable.ic_calendar_month_24,
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_all)) },
            selected = filterBundle.dateAdded is PrefRecentVolumesFilter.DateAdded.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_in_range)) },
            selected = filterBundle.dateAdded is PrefRecentVolumesFilter.DateAdded.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = getDaysOfCurrentWeek().run {
                            PrefRecentVolumesFilter.DateAdded.InRange(
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
            derivedStateOf { filterBundle.dateAdded is PrefRecentVolumesFilter.DateAdded.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateAdded = filterBundle.dateAdded) {
                PrefRecentVolumesFilter.DateAdded.Unknown -> getDaysOfCurrentWeek()
                is PrefRecentVolumesFilter.DateAdded.InRange -> dateAdded.run {
                    Pair(
                        start,
                        end
                    )
                }
            },
            enabled = enabled,
            onClick = {
                when (val dateAdded = filterBundle.dateAdded) {
                    PrefRecentVolumesFilter.DateAdded.Unknown -> throw IllegalStateException()
                    is PrefRecentVolumesFilter.DateAdded.InRange -> dateAdded.run {
                        onDatePickerDialogShow(VolumesDatePickerType.DateAdded, Pair(start, end))
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewVolumesFilter() {
    var filterBundle by remember {
        mutableStateOf(
            PrefRecentVolumesFilterBundle(
                dateAdded = PrefRecentVolumesFilter.DateAdded.Unknown,
            )
        )
    }
    OpenComicVineTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            volumesFilter(
                filterBundle = filterBundle,
                onFiltersChanged = { filterBundle = it },
                onDatePickerDialogShow = { _, _ -> },
            )
        }
    }
}
