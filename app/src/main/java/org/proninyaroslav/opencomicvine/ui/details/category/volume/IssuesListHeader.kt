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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.preferences.PrefSortDirection
import org.proninyaroslav.opencomicvine.data.preferences.PrefVolumeIssuesSort
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun IssuesListHeader(
    issuesCount: Int,
    currentSort: PrefVolumeIssuesSort,
    onSortChanged: (PrefVolumeIssuesSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        ) {
            Text(
                pluralStringResource(
                    R.plurals.details_volume_count_of_issues_template,
                    issuesCount,
                    issuesCount,
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )
            FilledTonalIconButton(onClick = { expanded = true }) {
                Icon(
                    painterResource(R.drawable.ic_sort_24),
                    contentDescription = stringResource(R.string.sort)
                )
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            IssuesSortMenu(
                currentSort = currentSort,
                expanded = expanded,
                onDismissRequest = { expanded = false },
                onSelected = {
                    expanded = false
                    onSortChanged(it)
                },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewIssuesListHeader() {
    var currentSort: PrefVolumeIssuesSort by remember {
        mutableStateOf(PrefVolumeIssuesSort.StoreDate(direction = PrefSortDirection.Asc))
    }
    OpenComicVineTheme {
        IssuesListHeader(
            currentSort = currentSort,
            issuesCount = 3,
            onSortChanged = { currentSort = it },
        )
    }
}
