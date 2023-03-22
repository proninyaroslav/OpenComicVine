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

package org.proninyaroslav.opencomicvine.ui.wiki.category.filter

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
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiCharactersSort
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDatePickerItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterRadioButtonItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterTextFieldItem
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

enum class CharactersDatePickerType {
    Unknown,
    DateAdded,
    DateLastUpdated,
}

fun LazyListScope.charactersFilter(
    sort: PrefWikiCharactersSort,
    filterBundle: PrefWikiCharactersFilterBundle,
    onSortChanged: (PrefWikiCharactersSort) -> Unit,
    onFiltersChanged: (PrefWikiCharactersFilterBundle) -> Unit,
    onDatePickerDialogShow: (CharactersDatePickerType, Pair<Date, Date>) -> Unit,
) {
    sort(sort, onSortChanged)

    genderFilter(filterBundle, onFiltersChanged)

    nameFilter(filterBundle, onFiltersChanged)

    dateAddedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)

    dateLastUpdatedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)
}

private fun LazyListScope.nameFilter(
    filterBundle: PrefWikiCharactersFilterBundle,
    onFiltersChanged: (PrefWikiCharactersFilterBundle) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_name),
            icon = R.drawable.ic_abc_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    item {
        FilterTextFieldItem(
            placeholder = { Text(stringResource(R.string.filter_name_placeholder)) },
            value = when (val name = filterBundle.name) {
                PrefWikiCharactersFilter.Name.Unknown -> ""
                is PrefWikiCharactersFilter.Name.Contains -> name.nameValue
            },
            onValueChanged = {
                onFiltersChanged(
                    filterBundle.copy(
                        name = if (it.isEmpty()) {
                            PrefWikiCharactersFilter.Name.Unknown
                        } else {
                            PrefWikiCharactersFilter.Name.Contains(it)
                        }
                    )
                )
            },
        )
    }
}

private fun LazyListScope.genderFilter(
    filterBundle: PrefWikiCharactersFilterBundle,
    onFiltersChanged: (PrefWikiCharactersFilterBundle) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.gender),
            icon = R.drawable.ic_face_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    items(genderFilterItems) {
        FilterRadioButtonItem(
            label = { Text(stringResource(it.label)) },
            selected = filterBundle.gender == it.filter,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        gender = it.filter as PrefWikiCharactersFilter.Gender
                    )
                )
            }
        )
    }
}

private fun LazyListScope.sort(
    sort: PrefWikiCharactersSort,
    onSortChanged: (PrefWikiCharactersSort) -> Unit,
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

private fun LazyListScope.dateLastUpdatedFilter(
    filterBundle: PrefWikiCharactersFilterBundle,
    onFiltersChanged: (PrefWikiCharactersFilterBundle) -> Unit,
    onDatePickerDialogShow: (CharactersDatePickerType, Pair<Date, Date>) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_date_last_updated),
            icon = R.drawable.ic_calendar_month_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_last_updated_all)) },
            selected = filterBundle.dateLastUpdated == PrefWikiCharactersFilter.DateLastUpdated.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_last_updated_in_range)) },
            selected = filterBundle.dateLastUpdated is PrefWikiCharactersFilter.DateLastUpdated.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateLastUpdated = getDaysOfCurrentWeek().run {
                            PrefWikiCharactersFilter.DateLastUpdated.InRange(
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
            derivedStateOf { filterBundle.dateLastUpdated is PrefWikiCharactersFilter.DateLastUpdated.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateLastUpdated = filterBundle.dateLastUpdated) {
                PrefWikiCharactersFilter.DateLastUpdated.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiCharactersFilter.DateLastUpdated.InRange -> {
                    dateLastUpdated.run { Pair(start, end) }
                }
            },
            enabled = enabled,
            onClick = {
                when (val dateLastUpdated = filterBundle.dateLastUpdated) {
                    PrefWikiCharactersFilter.DateLastUpdated.Unknown -> throw IllegalStateException()
                    is PrefWikiCharactersFilter.DateLastUpdated.InRange -> dateLastUpdated.run {
                        onDatePickerDialogShow(
                            CharactersDatePickerType.DateLastUpdated,
                            Pair(start, end)
                        )
                    }
                }
            }
        )
    }
}

private fun LazyListScope.dateAddedFilter(
    filterBundle: PrefWikiCharactersFilterBundle,
    onFiltersChanged: (PrefWikiCharactersFilterBundle) -> Unit,
    onDatePickerDialogShow: (CharactersDatePickerType, Pair<Date, Date>) -> Unit,
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
            selected = filterBundle.dateAdded == PrefWikiCharactersFilter.DateAdded.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = PrefWikiCharactersFilter.DateAdded.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_in_range)) },
            selected = filterBundle.dateAdded is PrefWikiCharactersFilter.DateAdded.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = getDaysOfCurrentWeek().run {
                            PrefWikiCharactersFilter.DateAdded.InRange(
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
            derivedStateOf { filterBundle.dateAdded is PrefWikiCharactersFilter.DateAdded.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateAdded = filterBundle.dateAdded) {
                PrefWikiCharactersFilter.DateAdded.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiCharactersFilter.DateAdded.InRange -> dateAdded.run { Pair(start, end) }
            },
            enabled = enabled,
            onClick = {
                when (val dateAdded = filterBundle.dateAdded) {
                    PrefWikiCharactersFilter.DateAdded.Unknown -> throw IllegalStateException()
                    is PrefWikiCharactersFilter.DateAdded.InRange -> dateAdded.run {
                        onDatePickerDialogShow(CharactersDatePickerType.DateAdded, Pair(start, end))
                    }
                }
            }
        )
    }
}

private data class CharactersSortItem(
    @StringRes val label: Int,
    val sort: PrefWikiCharactersSort,
)

private data class GenderFilterItem(
    @StringRes val label: Int,
    val filter: PrefWikiCharactersFilter,
)

private val sortItems = listOf(
    CharactersSortItem(
        label = R.string.sort_without_sorting,
        sort = PrefWikiCharactersSort.Unknown,
    ),
    CharactersSortItem(
        label = R.string.sort_alphabetical_asc,
        sort = PrefWikiCharactersSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
    ),
    CharactersSortItem(
        label = R.string.sort_alphabetical_desc,
        sort = PrefWikiCharactersSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
    ),
    CharactersSortItem(
        label = R.string.sort_date_last_updated_desc,
        sort = PrefWikiCharactersSort.DateLastUpdated(
            direction = PrefSortDirection.Desc,
        )
    ),
    CharactersSortItem(
        label = R.string.sort_date_last_updated_asc,
        sort = PrefWikiCharactersSort.DateLastUpdated(
            direction = PrefSortDirection.Asc,
        )
    ),
    CharactersSortItem(
        label = R.string.sort_date_added_desc,
        sort = PrefWikiCharactersSort.DateAdded(
            direction = PrefSortDirection.Desc,
        )
    ),
    CharactersSortItem(
        label = R.string.sort_date_added_asc,
        sort = PrefWikiCharactersSort.DateAdded(
            direction = PrefSortDirection.Asc,
        )
    ),
)

private val genderFilterItems = listOf(
    GenderFilterItem(
        label = R.string.filter_gender_all,
        filter = PrefWikiCharactersFilter.Gender.Unknown,
    ),
    GenderFilterItem(
        label = R.string.filter_gender_other,
        filter = PrefWikiCharactersFilter.Gender.Other,
    ),
    GenderFilterItem(
        label = R.string.filter_gender_male,
        filter = PrefWikiCharactersFilter.Gender.Male,
    ),
    GenderFilterItem(
        label = R.string.filter_gender_female,
        filter = PrefWikiCharactersFilter.Gender.Female,
    ),
)

@Preview(showBackground = true)
@Composable
private fun PreviewCharactersFilter() {
    var sort: PrefWikiCharactersSort by remember {
        mutableStateOf(
            PrefWikiCharactersSort.Alphabetical(
                direction = PrefSortDirection.Asc,
            )
        )
    }
    var filterBundle by remember {
        mutableStateOf(
            PrefWikiCharactersFilterBundle(
                gender = PrefWikiCharactersFilter.Gender.Unknown,
                name = PrefWikiCharactersFilter.Name.Unknown,
                dateAdded = PrefWikiCharactersFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiCharactersFilter.DateLastUpdated.Unknown,
            )
        )
    }
    OpenComicVineTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            charactersFilter(
                sort = sort,
                filterBundle = filterBundle,
                onSortChanged = { sort = it },
                onFiltersChanged = { filterBundle = it },
                onDatePickerDialogShow = { _, _ -> },
            )
        }
    }
}
