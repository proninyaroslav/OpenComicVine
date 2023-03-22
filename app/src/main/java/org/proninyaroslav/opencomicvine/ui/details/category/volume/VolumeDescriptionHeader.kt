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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.ui.details.*
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.text.DateFormat

@Composable
fun VolumeDescriptionHeader(
    details: VolumeDetails?,
    isFullLoaded: Boolean,
    isExpandedWidth: Boolean,
    onImageClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
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
                    text = details?.startYear?.let {
                        stringResource(
                            R.string.details_volume_started_in_template,
                            it,
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
private fun PreviewVolumeDescriptionHeader_Loading() {
    val isExpandedWidth = false
    OpenComicVineTheme {
        VolumeDescriptionHeader(
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
private fun PreviewVolumeDescriptionHeader_LoadingWithExpandedWidth() {
    val isExpandedWidth = true
    OpenComicVineTheme {
        VolumeDescriptionHeader(
            details = null,
            isExpandedWidth = isExpandedWidth,
            isFullLoaded = false,
            onImageClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
