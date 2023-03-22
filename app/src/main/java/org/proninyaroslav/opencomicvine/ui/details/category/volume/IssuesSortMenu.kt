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

package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.annotation.StringRes
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.data.preferences.PrefVolumeIssuesSort
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

private data class SortItem(
    @StringRes val name: Int,
    val type: PrefVolumeIssuesSort,
)

private val sortItems = listOf(
    SortItem(
        name = R.string.sort_store_date_asc,
        type = PrefVolumeIssuesSort.StoreDate(direction = PrefSortDirection.Asc),
    ),
    SortItem(
        name = R.string.sort_store_date_desc,
        type = PrefVolumeIssuesSort.StoreDate(direction = PrefSortDirection.Desc),
    ),
)

@Composable
fun IssuesSortMenu(
    currentSort: PrefVolumeIssuesSort,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (PrefVolumeIssuesSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        sortItems.onEach {
            DropdownMenuItem(
                text = { Text(stringResource(it.name)) },
                onClick = { onSelected(it.type) },
                leadingIcon = {
                    RadioButton(
                        selected = it.type == currentSort,
                        onClick = { onSelected(it.type) },
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewIssuesSortMenu() {
    OpenComicVineTheme {
        IssuesSortMenu(
            currentSort = PrefVolumeIssuesSort.StoreDate(direction = PrefSortDirection.Asc),
            expanded = true,
            onDismissRequest = {},
            onSelected = {},
        )
    }
}
