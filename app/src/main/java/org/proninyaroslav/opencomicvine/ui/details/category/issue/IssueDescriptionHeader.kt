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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.IssueDetails
import org.proninyaroslav.opencomicvine.ui.details.*
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.text.DateFormat

@Composable
fun IssueDescriptionHeader(
    details: IssueDetails?,
    isFullLoaded: Boolean,
    isExpandedWidth: Boolean,
    onImageClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    val coverDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    DetailsHeader(
        image = {
            DetailsImage(
                image = details?.image,
                imageDescription = details?.name,
                isExpandedWidth = isExpandedWidth,
                onClick = onImageClick,
            )
        },
        shortDescription = {
            DetailsPlaceholderText(
                text = details?.descriptionShort,
                visible = details == null || !isFullLoaded,
                isExpandedWidth = isExpandedWidth,
            )
        },
        shortInformation = {
            DetailsShortInfo {
                DetailsPlaceholderText(
                    text = details?.coverDate?.let {
                        stringResource(
                            R.string.details_issue_released_on_date_template,
                            coverDateFormat.format(it),
                        )
                    },
                    visible = details == null || !isFullLoaded,
                    isExpandedWidth = isExpandedWidth,
                )
                DetailsSummaryText(
                    text = details?.dateAdded?.let {
                        stringResource(
                            R.string.details_date_added_template,
                            dateFormat.format(it)
                        )
                    },
                    icon = R.drawable.ic_calendar_month_24,
                    isExpandedWidth = isExpandedWidth,
                )
                DetailsSummaryText(
                    text = details?.dateLastUpdated?.let {
                        stringResource(
                            R.string.details_date_last_updated_template,
                            dateFormat.format(it)
                        )
                    },
                    icon = R.drawable.ic_calendar_month_24,
                    isExpandedWidth = isExpandedWidth,
                )
            }
        },
        modifier = modifier,
    )
}

@Preview(name = "Loading")
@Composable
private fun PreviewIssueDescriptionHeader_Loading() {
    val isExpandedWidth = false
    OpenComicVineTheme {
        IssueDescriptionHeader(
            details = null,
            isExpandedWidth = isExpandedWidth,
            isFullLoaded = false,
            onImageClick = {},
        )
    }
}

@Preview(
    name = "Loading with expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewIssueDescriptionHeader_LoadingWithExpandedWidth() {
    val isExpandedWidth = true
    OpenComicVineTheme {
        IssueDescriptionHeader(
            details = null,
            isExpandedWidth = isExpandedWidth,
            isFullLoaded = false,
            onImageClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
