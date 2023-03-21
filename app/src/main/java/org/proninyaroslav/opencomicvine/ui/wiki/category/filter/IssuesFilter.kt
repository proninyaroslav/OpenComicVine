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
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiIssuesFilter
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiIssuesFilterBundle
import org.proninyaroslav.opencomicvine.data.preferences.PrefWikiIssuesSort
import org.proninyaroslav.opencomicvine.model.getDaysOfCurrentWeek
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterDatePickerItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterRadioButtonItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterTextFieldItem
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.util.*

enum class IssuesDatePickerType {
    Unknown,
    DateAdded,
    DateLastUpdated,
    CoverDate,
    StoreDate,
}

fun LazyListScope.issuesFilter(
    sort: PrefWikiIssuesSort,
    filterBundle: PrefWikiIssuesFilterBundle,
    onSortChanged: (PrefWikiIssuesSort) -> Unit,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
    onDatePickerDialogShow: (IssuesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    sort(sort, onSortChanged)

    nameFilter(filterBundle, onFiltersChanged)

    issueNumberFilter(filterBundle, onFiltersChanged)

    dateAddedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)

    dateLastUpdatedFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)

    coverDateFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)

    storeDateFilter(filterBundle, onFiltersChanged, onDatePickerDialogShow)
}

private fun LazyListScope.sort(
    sort: PrefWikiIssuesSort,
    onSortChanged: (PrefWikiIssuesSort) -> Unit,
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

private fun LazyListScope.issueNumberFilter(
    filterBundle: PrefWikiIssuesFilterBundle,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_issue_number),
            icon = R.drawable.ic_tag_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    item {
        FilterTextFieldItem(
            placeholder = { Text(stringResource(R.string.filter_issue_number)) },
            value = when (val name = filterBundle.issueNumber) {
                PrefWikiIssuesFilter.IssueNumber.Unknown -> ""
                is PrefWikiIssuesFilter.IssueNumber.Contains -> name.issueNumberValue
            },
            onValueChanged = {
                onFiltersChanged(
                    filterBundle.copy(
                        issueNumber = if (it.isEmpty()) {
                            PrefWikiIssuesFilter.IssueNumber.Unknown
                        } else {
                            PrefWikiIssuesFilter.IssueNumber.Contains(it)
                        }
                    )
                )
            },
        )
    }
}

private fun LazyListScope.nameFilter(
    filterBundle: PrefWikiIssuesFilterBundle,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
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
                PrefWikiIssuesFilter.Name.Unknown -> ""
                is PrefWikiIssuesFilter.Name.Contains -> name.nameValue
            },
            onValueChanged = {
                onFiltersChanged(
                    filterBundle.copy(
                        name = if (it.isEmpty()) {
                            PrefWikiIssuesFilter.Name.Unknown
                        } else {
                            PrefWikiIssuesFilter.Name.Contains(it)
                        }
                    )
                )
            },
        )
    }
}

private fun LazyListScope.dateAddedFilter(
    filterBundle: PrefWikiIssuesFilterBundle,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
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
            selected = filterBundle.dateAdded == PrefWikiIssuesFilter.DateAdded.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_added_in_range)) },
            selected = filterBundle.dateAdded is PrefWikiIssuesFilter.DateAdded.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateAdded = getDaysOfCurrentWeek().run {
                            PrefWikiIssuesFilter.DateAdded.InRange(
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
            derivedStateOf { filterBundle.dateAdded is PrefWikiIssuesFilter.DateAdded.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateAdded = filterBundle.dateAdded) {
                PrefWikiIssuesFilter.DateAdded.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiIssuesFilter.DateAdded.InRange -> dateAdded.run { Pair(start, end) }
            },
            enabled = enabled,
            onClick = {
                when (val dateAdded = filterBundle.dateAdded) {
                    PrefWikiIssuesFilter.DateAdded.Unknown -> throw IllegalStateException()
                    is PrefWikiIssuesFilter.DateAdded.InRange -> dateAdded.run {
                        onDatePickerDialogShow(IssuesDatePickerType.DateAdded, Pair(start, end))
                    }
                }
            }
        )
    }
}

private fun LazyListScope.dateLastUpdatedFilter(
    filterBundle: PrefWikiIssuesFilterBundle,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
    onDatePickerDialogShow: (IssuesDatePickerType, Pair<Date, Date>) -> Unit,
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
            selected = filterBundle.dateLastUpdated == PrefWikiIssuesFilter.DateLastUpdated.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_date_last_updated_in_range)) },
            selected = filterBundle.dateLastUpdated is PrefWikiIssuesFilter.DateLastUpdated.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        dateLastUpdated = getDaysOfCurrentWeek().run {
                            PrefWikiIssuesFilter.DateLastUpdated.InRange(
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
            derivedStateOf { filterBundle.dateLastUpdated is PrefWikiIssuesFilter.DateLastUpdated.InRange }
        }
        FilterDatePickerItem(
            value = when (val dateLastUpdated = filterBundle.dateLastUpdated) {
                PrefWikiIssuesFilter.DateLastUpdated.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiIssuesFilter.DateLastUpdated.InRange -> {
                    dateLastUpdated.run { Pair(start, end) }
                }
            },
            enabled = enabled,
            onClick = {
                when (val dateLastUpdated = filterBundle.dateLastUpdated) {
                    PrefWikiIssuesFilter.DateLastUpdated.Unknown -> throw IllegalStateException()
                    is PrefWikiIssuesFilter.DateLastUpdated.InRange -> dateLastUpdated.run {
                        onDatePickerDialogShow(
                            IssuesDatePickerType.DateLastUpdated,
                            Pair(start, end)
                        )
                    }
                }
            }
        )
    }
}

private fun LazyListScope.coverDateFilter(
    filterBundle: PrefWikiIssuesFilterBundle,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
    onDatePickerDialogShow: (IssuesDatePickerType, Pair<Date, Date>) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.filter_cover_date),
            icon = R.drawable.ic_calendar_month_24,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_cover_date_all)) },
            selected = filterBundle.coverDate == PrefWikiIssuesFilter.CoverDate.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_cover_date_in_range)) },
            selected = filterBundle.coverDate is PrefWikiIssuesFilter.CoverDate.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        coverDate = getDaysOfCurrentWeek().run {
                            PrefWikiIssuesFilter.CoverDate.InRange(
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
            derivedStateOf { filterBundle.coverDate is PrefWikiIssuesFilter.CoverDate.InRange }
        }
        FilterDatePickerItem(
            value = when (val coverDate = filterBundle.coverDate) {
                PrefWikiIssuesFilter.CoverDate.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiIssuesFilter.CoverDate.InRange -> {
                    coverDate.run { Pair(start, end) }
                }
            },
            enabled = enabled,
            onClick = {
                when (val coverDate = filterBundle.coverDate) {
                    PrefWikiIssuesFilter.CoverDate.Unknown -> throw IllegalStateException()
                    is PrefWikiIssuesFilter.CoverDate.InRange -> coverDate.run {
                        onDatePickerDialogShow(IssuesDatePickerType.CoverDate, Pair(start, end))
                    }
                }
            }
        )
    }
}

private fun LazyListScope.storeDateFilter(
    filterBundle: PrefWikiIssuesFilterBundle,
    onFiltersChanged: (PrefWikiIssuesFilterBundle) -> Unit,
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
            selected = filterBundle.storeDate == PrefWikiIssuesFilter.StoreDate.Unknown,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
                    )
                )
            }
        )
    }

    item {
        FilterRadioButtonItem(
            label = { Text(stringResource(R.string.filter_store_date_in_range)) },
            selected = filterBundle.storeDate is PrefWikiIssuesFilter.StoreDate.InRange,
            onClick = {
                onFiltersChanged(
                    filterBundle.copy(
                        storeDate = getDaysOfCurrentWeek().run {
                            PrefWikiIssuesFilter.StoreDate.InRange(
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
            derivedStateOf { filterBundle.storeDate is PrefWikiIssuesFilter.StoreDate.InRange }
        }
        FilterDatePickerItem(
            value = when (val storeDate = filterBundle.storeDate) {
                PrefWikiIssuesFilter.StoreDate.Unknown -> getDaysOfCurrentWeek()
                is PrefWikiIssuesFilter.StoreDate.InRange -> {
                    storeDate.run { Pair(start, end) }
                }
            },
            enabled = enabled,
            onClick = {
                when (val storeDate = filterBundle.storeDate) {
                    PrefWikiIssuesFilter.StoreDate.Unknown -> throw IllegalStateException()
                    is PrefWikiIssuesFilter.StoreDate.InRange -> storeDate.run {
                        onDatePickerDialogShow(IssuesDatePickerType.StoreDate, Pair(start, end))
                    }
                }
            }
        )
    }
}

private data class IssuesSortItem(
    @StringRes val label: Int,
    val sort: PrefWikiIssuesSort,
)

private val sortItems = listOf(
    IssuesSortItem(
        label = R.string.sort_without_sorting,
        sort = PrefWikiIssuesSort.Unknown,
    ),
    IssuesSortItem(
        label = R.string.sort_alphabetical_asc,
        sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Asc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_alphabetical_desc,
        sort = PrefWikiIssuesSort.Alphabetical(
            direction = PrefSortDirection.Desc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_date_last_updated_desc,
        sort = PrefWikiIssuesSort.DateLastUpdated(
            direction = PrefSortDirection.Desc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_date_last_updated_asc,
        sort = PrefWikiIssuesSort.DateLastUpdated(
            direction = PrefSortDirection.Asc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_date_added_desc,
        sort = PrefWikiIssuesSort.DateAdded(
            direction = PrefSortDirection.Desc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_date_added_asc,
        sort = PrefWikiIssuesSort.DateAdded(
            direction = PrefSortDirection.Asc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_cover_date_desc,
        sort = PrefWikiIssuesSort.CoverDate(
            direction = PrefSortDirection.Desc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_cover_date_asc,
        sort = PrefWikiIssuesSort.CoverDate(
            direction = PrefSortDirection.Asc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_store_date_desc,
        sort = PrefWikiIssuesSort.StoreDate(
            direction = PrefSortDirection.Desc,
        )
    ),
    IssuesSortItem(
        label = R.string.sort_store_date_asc,
        sort = PrefWikiIssuesSort.StoreDate(
            direction = PrefSortDirection.Asc,
        )
    ),
)

@Preview(showBackground = true)
@Composable
private fun PreviewIssuesFilter() {
    var sort: PrefWikiIssuesSort by remember {
        mutableStateOf(
            PrefWikiIssuesSort.Alphabetical(
                direction = PrefSortDirection.Asc,
            )
        )
    }
    var filterBundle by remember {
        mutableStateOf(
            PrefWikiIssuesFilterBundle(
                name = PrefWikiIssuesFilter.Name.Unknown,
                dateAdded = PrefWikiIssuesFilter.DateAdded.Unknown,
                dateLastUpdated = PrefWikiIssuesFilter.DateLastUpdated.Unknown,
                coverDate = PrefWikiIssuesFilter.CoverDate.Unknown,
                storeDate = PrefWikiIssuesFilter.StoreDate.Unknown,
                issueNumber = PrefWikiIssuesFilter.IssueNumber.Unknown,
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
                onSortChanged = { sort = it },
                onFiltersChanged = { filterBundle = it },
                onDatePickerDialogShow = { _, _ -> },
            )
        }
    }
}