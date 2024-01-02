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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.IssueInfo
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.components.list.CardCellSize
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.text.DateFormat
import java.util.*

@Composable
fun IssueCard(
    modifier: Modifier = Modifier,
    issueInfo: IssueInfo?,
    imageForceStretchHeight: Boolean = true,
    compact: Boolean = false,
    onClick: () -> Unit,
) {
    InnerIssueCard(
        volumeName = issueInfo?.volume?.name ?: "",
        issueNumber = issueInfo?.issueNumber ?: "",
        issueName = issueInfo?.name,
        coverDate = issueInfo?.coverDate,
        imageUrl = issueInfo?.image?.smallUrl,
        fallbackImageUrl = issueInfo?.image?.originalUrl,
        imageForceStretchHeight = imageForceStretchHeight,
        compact = compact,
        onClick = onClick,
        loading = issueInfo == null,
        modifier = modifier,
    )
}

@Composable
private fun InnerIssueCard(
    modifier: Modifier = Modifier,
    volumeName: String,
    issueNumber: String,
    issueName: String?,
    coverDate: Date?,
    imageUrl: String?,
    fallbackImageUrl: String?,
    imageForceStretchHeight: Boolean = true,
    compact: Boolean = false,
    loading: Boolean,
    onClick: () -> Unit,
) {
    var compactHeight by remember { mutableStateOf(CardCellSize.Adaptive.Small.minSize * 2) }
    val title = if (issueName == null) {
        stringResource(R.string.issue_title_template_without_name, volumeName, issueNumber)
    } else {
        stringResource(R.string.issue_title_template, volumeName, issueNumber, issueName)
    }
    val titleStyle = MaterialTheme.typography.titleMedium
    val maxTitleLines = 4
    val maxDateLines = 2
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)

    CardWithImage(
        imageUrl = imageUrl,
        fallbackImageUrl = fallbackImageUrl,
        imageDescription = title,
        placeholder = R.drawable.placeholder_small,
        imageForceStretchHeight = imageForceStretchHeight,
        modifier = modifier.then(
            if (compact) {
                Modifier.height(compactHeight)
            } else {
                Modifier
            }
        ),
        onImageWidthChanged = {
            if (compact) {
                compactHeight = it * 2
            }
        },
        onClick = onClick,
    ) {
        if (!compact) {
            Text(
                title,
                maxLines = maxTitleLines,
                style = titleStyle,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = if (coverDate != null) 8.dp else 16.dp,
                    )
                    .heightIn(
                        min = if (coverDate != null) {
                            0.dp
                        } else {
                            titleStyle.calculateTextHeight(maxLines = maxDateLines)
                        }
                    )
                    .align(Alignment.Start)
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
            if (!loading) {
                coverDate?.let {
                    CardSummaryText(
                        text = dateFormat.format(coverDate),
                        icon = R.drawable.ic_calendar_month_24,
                        maxLines = maxDateLines,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp,
                            )
                            .wrapContentHeight(Alignment.Top),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewIssueCard() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    issueName = "Issue name",
                    volumeName = "Volume name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Compact")
@Composable
fun PreviewIssueCard_Compact() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    issueName = "Issue name",
                    volumeName = "Volume name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    compact = true,
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Without cover date")
@Composable
fun PreviewIssueCard_WithoutCoverDate() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    volumeName = "Volume name",
                    issueNumber = "1",
                    issueName = "Issue name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = null,
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Without issue name")
@Composable
fun PreviewIssueCard_WithoutIssueName() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    volumeName = "Volume name",
                    issueNumber = "1",
                    issueName = null,
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview("Name overflow")
@Composable
fun PreviewIssueCard_NameOverflow() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    volumeName = "Very very very long volume name",
                    issueNumber = "1",
                    issueName = "Issue name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                )
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
fun PreviewIssueCard_Loading() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    issueName = "Issue name",
                    volumeName = "Volume name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = true,
                )
            }
        }
    }
}

@Preview("Loading compact")
@Composable
fun PreviewIssueCard_LoadingCompact() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerIssueCard(
                    issueName = "Issue name",
                    volumeName = "Volume name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    compact = true,
                    onClick = {},
                    loading = true,
                )
            }
        }
    }
}
