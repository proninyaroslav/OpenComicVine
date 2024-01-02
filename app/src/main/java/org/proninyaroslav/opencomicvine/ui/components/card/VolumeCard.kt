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

package org.proninyaroslav.opencomicvine.ui.components.card

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.VolumeInfo
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun VolumeCard(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfo?,
    imageForceStretchHeight: Boolean = true,
    compact: Boolean = false,
    onClick: () -> Unit,
) {
    InnerVolumeCard(
        volumeName = volumeInfo?.name ?: "",
        countOfIssues = volumeInfo?.countOfIssues ?: 0,
        startYear = volumeInfo?.startYear,
        publisherName = volumeInfo?.publisher?.name,
        imageUrl = volumeInfo?.image?.smallUrl,
        fallbackImageUrl = volumeInfo?.image?.originalUrl,
        imageForceStretchHeight = imageForceStretchHeight,
        compact = compact,
        onClick = onClick,
        loading = volumeInfo == null,
        modifier = modifier,
    )
}

@Composable
private fun InnerVolumeCard(
    modifier: Modifier = Modifier,
    volumeName: String,
    countOfIssues: Int,
    startYear: String?,
    publisherName: String?,
    imageUrl: String?,
    fallbackImageUrl: String?,
    imageForceStretchHeight: Boolean = true,
    compact: Boolean = false,
    loading: Boolean,
    onClick: () -> Unit,
) {
    var compactHeight by remember { mutableStateOf(CardCellSize.Adaptive.Small.minSize * 2) }
    val mod = if (compact) modifier.height(compactHeight) else modifier

    val titleStyle = MaterialTheme.typography.titleMedium
    val maxTitleLines = 4

    CardWithImage(
        imageUrl = imageUrl,
        fallbackImageUrl = fallbackImageUrl,
        imageDescription = volumeName,
        placeholder = R.drawable.placeholder_small,
        imageForceStretchHeight = imageForceStretchHeight,
        modifier = mod,
        onImageWidthChanged = {
            if (compact) {
                compactHeight = it * 2
            }
        },
        onClick = onClick,
    ) {
        if (!compact) {
            val countOfIssuesStr = pluralStringResource(
                R.plurals.volume_count_of_issues_template,
                countOfIssues,
                countOfIssues,
            )
            Text(
                volumeName,
                maxLines = maxTitleLines,
                style = titleStyle,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp,
                    )
                    .wrapContentHeight(Alignment.CenterVertically)
                    .defaultPlaceholder(visible = loading)
                    .then(
                        if (loading) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier
                        }
                    ),
            )
            startYear?.let {
                CardSummaryText(
                    text = startYear,
                    icon = R.drawable.ic_calendar_month_24,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp,
                        )
                        .wrapContentHeight(Alignment.Top)
                        .defaultPlaceholder(visible = loading)
                        .then(
                            if (loading) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier
                            }
                        ),
                )
            }
            publisherName?.let {
                CardSummaryText(
                    text = publisherName,
                    icon = R.drawable.ic_groups_24,
                    maxLines = 2,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp,
                        )
                        .wrapContentHeight(Alignment.Top)
                        .defaultPlaceholder(visible = loading)
                        .then(
                            if (loading) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier
                            }
                        ),
                )
            }
            CardSummaryText(
                text = countOfIssuesStr,
                icon = R.drawable.ic_library_books_24,
                maxLines = 2,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    )
                    .wrapContentHeight(Alignment.Top)
                    .defaultPlaceholder(visible = loading)
                    .then(
                        if (loading) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier
                        }
                    ),
            )
        }
    }
}

@Preview
@Composable
fun PreviewVolumeCard() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeCard(
                    volumeName = "Volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    countOfIssues = 10,
                    publisherName = "Publisher Name",
                    startYear = "2022",
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Compact")
@Composable
fun PreviewVolumeCard_Compact() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeCard(
                    volumeName = "Volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    countOfIssues = 10,
                    publisherName = "Publisher Name",
                    startYear = "2022",
                    compact = true,
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Without start year")
@Composable
fun PreviewVolumeCard_WithoutStartYear() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeCard(
                    volumeName = "Volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    countOfIssues = 10,
                    publisherName = "Publisher Name",
                    startYear = null,
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Name overflow")
@Composable
fun PreviewVolumeCard_NameOverflow() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeCard(
                    volumeName = "Very very very long volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    countOfIssues = 10,
                    publisherName = "Publisher Name",
                    startYear = "2022",
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
fun PreviewVolumeCard_Loading() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeCard(
                    volumeName = "Volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    countOfIssues = 10,
                    publisherName = "Publisher Name",
                    startYear = "2022",
                    onClick = {},
                    loading = true,
                )
            }
        }
    }
}

@Preview("Loading compact")
@Composable
fun PreviewVolumeCard_LoadingCompact() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeCard(
                    volumeName = "Volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    countOfIssues = 10,
                    publisherName = "Publisher Name",
                    startYear = "2022",
                    compact = true,
                    onClick = {},
                    loading = true,
                )
            }
        }
    }
}
