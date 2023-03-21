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
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiVolumesSort
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDatePickerItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterRadioButtonItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterTextFieldItem
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

enum class VolumesDatePickerType {
    Unknown,
    DateAdded,
    DateLastUpdated,
}

fun LazyListScope.volumesFilter(
    sort: PrefWikiVolumesSort,
    filterBundle: PrefWikiVolumesFilterBundle,
    onSortChanged: (PrefWikiVolumesSort) -> Unit,
    onFiltersChanged: (PrefWikiVolumesFilterBundle) -> Unit,
    onDatePickerDialogShow: (VolumesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    sort(sort, onSortChanged)

    nameFilter(filterBundle, onFiltersChanged)

    dateAddedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)

    dateLastUpdatedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)
}

private fun LazyListScope.nameFilter(
    filterBundle: PrefWikiVolumesFilterBundle,
    onFiltersChanged: (PrefWikiVolumesFilterBundle) -> Unit,
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
                PrefWikiVolumesFilter.Name.Unknown -> ""
                is PrefWikiVolumesFilter.Name.Contains -> name.nameValue
            },
            onValueChanged = {
                onFiltersChanged(
                    filterBundle.copy(
                        name = if (it.isEmpty()) {
                            PrefWikiVolumesFilter.Name.Unknown
                        } else {
                            PrefWikiVolumesFilter.Name.Contains(it)
                        }
                    )
                )
            },
        )
    }
}

private fun LazyListScope.sort(
    sort: PrefWikiVolumesSort,
    onSortChanged: (PrefWikiVolumesSort) -> Unit,
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
    filterBundle: PrefWikiVolumesFilterBundle,
    onFiltersChanged: (PrefWikiVolumesFilterBundle) -> Unit,
    onDatePickerDialogShow: (VolumesDatePickerType, Pair<Date, Date>) -> Unit,
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
            selected = filterBundle.dateLastUpdated == PrefWikiVolumesFilter.DateLastUpdated.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_last_updated_in_range)) },
            selected = filterBundle.dateLastUpdated is PrefWikiVolumesFilter.DateLastUpdated.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateLastUpdated = getDaysOfCurrentWeek().run {
                            PrefWikiVolumesFilter.DateLastUpdated.InRange(
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
            derivedStateOf { filterBundle.dateLastUpdated is PrefWikiVolumesFilter.DateLastUpdated.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateLastUpdated = filterBundle.dateLastUpdated) {
                PrefWikiVolumesFilter.DateLastUpdated.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiVolumesFilter.DateLastUpdated.InRange -> {
                    dateLastUpdated.run { Pair(start, end) }
                }
            },
            enabled = enabled,
            onClick = {
                when (val dateLastUpdated = filterBundle.dateLastUpdated) {
                    PrefWikiVolumesFilter.DateLastUpdated.Unknown -> throw IllegalStateException()
                    is PrefWikiVolumesFilter.DateLastUpdated.InRange -> dateLastUpdated.run {
                        onDatePickerDialogShow(
                            VolumesDatePickerType.DateLastUpdated,
                            Pair(start, end)
                        )
                    }
                }
            }
        )
    }
}

private fun LazyListScope.dateAddedFilter(
    filterBundle: PrefWikiVolumesFilterBundle,
    onFiltersChanged: (PrefWikiVolumesFilterBundle) -> Unit,
    onDatePickerDialogShow: (VolumesDatePickerType, Pair<Date, Date>) -> Unit,
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
            selected = filterBundle.dateAdded == PrefWikiVolumesFilter.DateAdded.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_in_range)) },
            selected = filterBundle.dateAdded is PrefWikiVolumesFilter.DateAdded.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = getDaysOfCurrentWeek().run {
                            PrefWikiVolumesFilter.DateAdded.InRange(
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
            derivedStateOf { filterBundle.dateAdded is PrefWikiVolumesFilter.DateAdded.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateAdded = filterBundle.dateAdded) {
                PrefWikiVolumesFilter.DateAdded.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiVolumesFilter.DateAdded.InRange -> dateAdded.run { Pair(start, end) }
            },
            enabled = enabled,
            onClick = {
                when (val dateAdded = filterBundle.dateAdded) {
                    PrefWikiVolumesFilter.DateAdded.Unknown -> throw IllegalStateException()
                    is PrefWikiVolumesFilter.DateAdded.InRange -> dateAdded.run {
                        onDatePickerDialogShow(VolumesDatePickerType.DateAdded, Pair(start, end))
                    }
                }
            }
        )
    }
}

private data class VolumesSortItem(
    @StringRes val label: Int,
    val sort: PrefWikiVolumesSort,
)

private val sortItems = listOf(
    VolumesSortItem(
        label = R.string.sort_without_sorting,
        sort = PrefWikiVolumesSort.Unknown,
    ),
    VolumesSortItem(
        label = R.string.sort_alphabetical_asc,
        sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
    ),
    VolumesSortItem(
        label = R.string.sort_alphabetical_desc,
        sort = PrefWikiVolumesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
    ),
    VolumesSortItem(
        label = R.string.sort_date_last_updated_desc,
        sort = PrefWikiVolumesSort.DateLastUpdated(
            direction = PrefSortDirection.Desc,
        )
    ),
    VolumesSortItem(
        label = R.string.sort_date_last_updated_asc,
        sort = PrefWikiVolumesSort.DateLastUpdated(
            direction = PrefSortDirection.Asc,
        )
    ),
    VolumesSortItem(
        label = R.string.sort_date_added_desc,
        sort = PrefWikiVolumesSort.DateAdded(
            direction = PrefSortDirection.Desc,
        )
    ),
    VolumesSortItem(
        label = R.string.sort_date_added_asc,
        sort = PrefWikiVolumesSort.DateAdded(
            direction = PrefSortDirection.Asc,
        )
    ),
)

@Preview(showBackground = true)
@Composable
private fun PreviewVolumesFilter() {
    var sort: PrefWikiVolumesSort by remember {
        mutableStateOf(
            PrefWikiVolumesSort.Alphabetical(
                direction = PrefSortDirection.Asc,
            )
        )
    }
    var filterBundle by remember {
        mutableStateOf(
            PrefWikiVolumesFilterBundle(
                name = PrefWikiVolumesFilter.Name.Unknown,
                dateAdded = PrefWikiVolumesFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiVolumesFilter.DateLastUpdated.Unknown,
            )
        )
    }
    OpenComicVineTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            volumesFilter(
                sort = sort,
                filterBundle = filterBundle,
                onSortChanged = { sort = it },
                onFiltersChanged = { filterBundle = it },
                onDatePickerDialogShow = { _, _ -> },
            )
        }
    }
}