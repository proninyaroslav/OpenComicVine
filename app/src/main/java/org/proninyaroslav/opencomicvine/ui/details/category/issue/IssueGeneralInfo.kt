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

package org.proninyaroslav.opencomicvine.ui.details.category.issue

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.IssueDetails
import org.proninyaroslav.opencomicvine.ui.components.chip.OpenContentChip
import org.proninyaroslav.opencomicvine.ui.details.DetailsGeneralInfo
import org.proninyaroslav.opencomicvine.ui.details.DetailsGeneralInfoItem
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import java.text.DateFormat

@Composable
fun IssueGeneralInfo(
    details: IssueDetails?,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsGeneralInfo(
        items = details?.let {
            toInfoItems(
                details = details,
                onLoadPage = onLoadPage,
            )
        } ?: emptyList(),
        loading = details == null,
        modifier = modifier,
    )
}

@Composable
private fun toInfoItems(
    onLoadPage: (DetailsPage) -> Unit,
    details: IssueDetails,
): List<DetailsGeneralInfoItem> =
    details.run {
        listOfNotNull(
            name?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_issue_name),
                    icon = R.drawable.ic_menu_book_24,
                    content = { Text(name) },
                )
            },
            DetailsGeneralInfoItem(
                label = stringResource(R.string.details_issue_volume),
                icon = R.drawable.ic_library_books_24,
                content = {
                    volume.run {
                        OpenContentChip(
                            label = name,
                            onClick = { onLoadPage(DetailsPage.Volume(id)) },
                        )
                    }
                },
            ),
            DetailsGeneralInfoItem(
                label = stringResource(R.string.details_issue_number),
                icon = R.drawable.ic_tag_24,
                content = { Text(issueNumber) },
            ),
            coverDate?.let {
                val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_issue_cover_date),
                    icon = R.drawable.ic_calendar_month_24,
                    content = { Text(dateFormat.format(coverDate)) },
                )
            },
            storeDate?.let {
                val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_issue_store_date),
                    icon = R.drawable.ic_calendar_month_24,
                    content = { Text(dateFormat.format(storeDate)) },
                )
            },
        )
    }
