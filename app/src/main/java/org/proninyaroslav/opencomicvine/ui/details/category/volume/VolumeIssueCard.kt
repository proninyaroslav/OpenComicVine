package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.IssueInfo
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.CustomBadge
import org.proninyaroslav.opencomicvine.ui.components.DefaultCustomBadgeSize
import org.proninyaroslav.opencomicvine.ui.components.card.CardSummaryText
import org.proninyaroslav.opencomicvine.ui.components.card.CardWithImage
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.text.DateFormat
import java.util.*

val VolumeIssueCardWidth = 160.dp
val VolumeIssueCardExpandedWidth = 192.dp

@Composable
fun VolumeIssueCard(
    modifier: Modifier = Modifier,
    issueInfo: IssueInfo?,
    isExpandedWidth: Boolean,
    onClick: () -> Unit,
) {
    InnerVolumeIssueCard(
        issueNumber = issueInfo?.issueNumber,
        issueName = issueInfo?.name,
        coverDate = issueInfo?.coverDate,
        imageUrl = issueInfo?.image?.smallUrl,
        fallbackImageUrl = issueInfo?.image?.originalUrl,
        onClick = onClick,
        loading = issueInfo == null,
        isExpandedWidth = isExpandedWidth,
        modifier = modifier,
    )
}

@Composable
private fun InnerVolumeIssueCard(
    modifier: Modifier = Modifier,
    issueNumber: String?,
    issueName: String?,
    coverDate: Date?,
    imageUrl: String?,
    fallbackImageUrl: String?,
    isExpandedWidth: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    val titleStyle = MaterialTheme.typography.titleMedium
    val maxTitleLines = 2
    val maxDateLines = 1
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    val cardWidth by remember {
        derivedStateOf {
            if (isExpandedWidth) VolumeIssueCardExpandedWidth else VolumeIssueCardWidth
        }
    }

    Box {
        CardWithImage(
            imageUrl = imageUrl,
            fallbackImageUrl = fallbackImageUrl,
            imageDescription = issueName,
            placeholder = R.drawable.placeholder_small,
            modifier = modifier
                .width(cardWidth)
                .padding(DefaultCustomBadgeSize / 2),
            imageForceStretchHeight = false,
            onClick = onClick,
        ) {
            Text(
                issueName ?: stringResource(R.string.no_name_placeholder),
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
                    .height(
                        if (coverDate != null) {
                            titleStyle.calculateTextHeight(maxLines = maxTitleLines)
                        } else {
                            titleStyle.calculateTextHeight(maxLines = maxTitleLines + maxDateLines)
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
        if (!loading) {
            issueNumber?.let {
                CustomBadge(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        stringResource(R.string.issue_number_template, issueNumber),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (isExpandedWidth) 18.sp else 16.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewVolumeIssueCard() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeIssueCard(
                    issueName = "Issue name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                    isExpandedWidth = false,
                )
            }
        }
    }
}

@Preview(name = "Expanded width")
@Composable
private fun PreviewVolumeIssueCard_ExpandedWidth() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeIssueCard(
                    issueName = "Issue name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                    isExpandedWidth = true,
                )
            }
        }
    }
}

@Preview("Without cover date")
@Composable
fun PreviewVolumeIssueCard_WithoutCoverDate() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeIssueCard(
                    issueNumber = "1",
                    issueName = "Issue name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = null,
                    onClick = {},
                    loading = false,
                    isExpandedWidth = false,
                )
            }
        }
    }
}

@Preview("Without issue name")
@Composable
fun PreviewVolumeIssueCard_WithoutIssueName() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeIssueCard(
                    issueNumber = "1",
                    issueName = null,
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                    isExpandedWidth = false,
                )
            }
        }
    }
}

@Preview("Name overflow")
@Composable
fun PreviewVolumeIssueCard_NameOverflow() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeIssueCard(
                    issueNumber = "1",
                    issueName = "Very very very long volume name",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = false,
                    isExpandedWidth = false,
                )
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
fun PreviewVolumeIssueCard_Loading() {
    OpenComicVineTheme {
        LazyColumn {
            item {
                InnerVolumeIssueCard(
                    issueName = "Issue name",
                    issueNumber = "1",
                    imageUrl = "https://dummyimage.com/320",
                    fallbackImageUrl = "https://dummyimage.com/320",
                    coverDate = Date(
                        GregorianCalendar(2022, 0, 1).timeInMillis
                    ),
                    onClick = {},
                    loading = true,
                    isExpandedWidth = false,
                )
            }
        }
    }
}