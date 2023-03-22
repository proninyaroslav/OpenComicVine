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

package org.proninyaroslav.opencomicvine.ui.favorites.category.filter

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterRadioButtonItem
import org.proninyaroslav.opencomicvine.ui.components.drawer.FilterSectionHeader
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

fun LazyListScope.favoritesFilter(
    sort: PrefFavoritesSort,
    onSortChanged: (PrefFavoritesSort) -> Unit,
) {
    sort(sort, onSortChanged)
}

private fun LazyListScope.sort(
    sort: PrefFavoritesSort,
    onSortChanged: (PrefFavoritesSort) -> Unit,
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

private data class SortItem(
    @StringRes val label: Int,
    val sort: PrefFavoritesSort,
)

private val sortItems = listOf(
    SortItem(
        label = R.string.sort_without_sorting,
        sort = PrefFavoritesSort.Unknown,
    ),
    SortItem(
        label = R.string.sort_date_added_desc,
        sort = PrefFavoritesSort.DateAdded(
            direction = PrefSortDirection.Desc,
        )
    ),
    SortItem(
        label = R.string.sort_date_added_asc,
        sort = PrefFavoritesSort.DateAdded(
            direction = PrefSortDirection.Asc,
        )
    ),
)

@Preview(showBackground = true)
@Composable
private fun PreviewFavoritesFilter() {
    var sort by remember {
        mutableStateOf<PrefFavoritesSort>(
            PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc)
        )
    }
    OpenComicVineTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            favoritesFilter(
                sort = sort,
                onSortChanged = { sort = it },
            )
        }
    }
}
