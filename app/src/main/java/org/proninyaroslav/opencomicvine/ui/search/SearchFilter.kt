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

package org.proninyaroslav.opencomicvine.ui.search

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.preferences.PrefSearchFilter
import org.proninyaroslav.opencomicvine.types.preferences.PrefSearchFilterBundle
import org.proninyaroslav.opencomicvine.types.preferences.PrefSearchResourceType
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterChipList
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterElevatedSelectableChipItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSelectableChipItem
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

fun LazyListScope.searchFilter(
    filter: PrefSearchFilterBundle,
    onFilterChange: (PrefSearchFilterBundle) -> Unit,
) {
    resourceTypeFilter(filter, onFilterChange)
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.resourceTypeFilter(
    bundle: PrefSearchFilterBundle,
    onFilterChange: (PrefSearchFilterBundle) -> Unit,
) {
    item {
        FilterSectionHeader(
            title = stringResource(R.string.search_result_type_filter),
            icon = R.drawable.ic_filter_list_24,
        )
    }

    item {
        FilterChipList {
            resourceTypeList.onEach { item ->
                val selected = when (val filter = item.filter) {
                    is PrefSearchFilter.Resources.Selected -> {
                        when (val res = bundle.resources) {
                            is PrefSearchFilter.Resources.Selected ->
                                res.resourceTypes.containsAll(filter.resourceTypes)
                            else -> false
                        }
                    }
                    else -> item.filter == bundle.resources
                }

                val onClick = {
                    val resources = when (val filter = item.filter) {
                        is PrefSearchFilter.Resources.Selected -> {
                            when (val res = bundle.resources) {
                                is PrefSearchFilter.Resources.Selected -> {
                                    val resTypes =
                                        if (res.resourceTypes.containsAll(filter.resourceTypes)) {
                                            res.resourceTypes - filter.resourceTypes
                                        } else {
                                            res.resourceTypes + filter.resourceTypes
                                        }
                                    if (resTypes.isEmpty()) {
                                        PrefSearchFilter.Resources.All
                                    } else {
                                        res.copy(resourceTypes = resTypes)
                                    }
                                }
                                else -> filter
                            }
                        }
                        else -> filter
                    }
                    onFilterChange(bundle.copy(resources = resources))
                }

                when (item.filter) {
                    PrefSearchFilter.Resources.All -> {
                        FilterElevatedSelectableChipItem(
                            label = { Text(stringResource(item.label)) },
                            selected = selected,
                            onClick = onClick,
                        )
                    }
                    else -> FilterSelectableChipItem(
                        label = { Text(stringResource(item.label)) },
                        selected = selected,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

private data class ResourceTypeItem(
    @StringRes val label: Int,
    val filter: PrefSearchFilter.Resources,
)

private val resourceTypeList = listOf(
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_all,
        filter = PrefSearchFilter.Resources.All,
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_characters,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Character)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_concepts,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Concept)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_episodes,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Episode)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_issues,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Issue)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_locations,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Location)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_things,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Object)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_people,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Person)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_series,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Series)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_story_arcs,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.StoryArc)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_teams,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Team)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_videos,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Video)
        ),
    ),
    ResourceTypeItem(
        label = R.string.search_filter_resource_type_volumes,
        filter = PrefSearchFilter.Resources.Selected(
            setOf(PrefSearchResourceType.Volume)
        ),
    ),
)

@Preview(showBackground = true)
@Composable
fun PreviewFavoritesFilter() {
    var filter by remember {
        mutableStateOf(
            PrefSearchFilterBundle(
                resources = PrefSearchFilter.Resources.All,
            )
        )
    }
    OpenComicVineTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            searchFilter(
                filter = filter,
                onFilterChange = { filter = it },
            )
        }
    }
}
