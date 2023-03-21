package org.proninyaroslav.opencomicvine.ui.details.category.volume

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.VolumeDetails
import org.proninyaroslav.opencomicvine.ui.components.chip.OpenContentChip
import org.proninyaroslav.opencomicvine.ui.details.DetailsGeneralInfo
import org.proninyaroslav.opencomicvine.ui.details.DetailsGeneralInfoItem
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage

@Composable
fun VolumeGeneralInfo(
    details: VolumeDetails?,
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
    details: VolumeDetails,
): List<DetailsGeneralInfoItem> =
    details.run {
        listOfNotNull(
            startYear?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_volume_start_year),
                    icon = R.drawable.ic_calendar_month_24,
                    content = { Text(startYear) },
                )
            },
            firstIssue?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_volume_first_issue),
                    icon = R.drawable.ic_menu_book_24,
                    content = {
                        firstIssue.run {
                            OpenContentChip(
                                label = buildTitle(),
                                onClick = { onLoadPage(DetailsPage.Issue(id)) },
                            )
                        }
                    },
                )
            },
            if (countOfIssues > 1) {
                lastIssue?.let {
                    DetailsGeneralInfoItem(
                        label = stringResource(R.string.details_volume_last_issue),
                        icon = R.drawable.ic_menu_book_24,
                        content = {
                            lastIssue.run {
                                OpenContentChip(
                                    label = buildTitle(),
                                    onClick = { onLoadPage(DetailsPage.Issue(id)) },
                                )
                            }
                        },
                    )
                }
            } else {
                null
            },
            publisher?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_publisher),
                    icon = R.drawable.ic_groups_24,
                    content = {
                        publisher.run {
                            OpenContentChip(
                                label = name,
                                onClick = { onLoadPage(DetailsPage.Publisher(id)) },
                            )
                        }
                    },
                )
            },
        )
    }

@Composable
fun VolumeDetails.Issue.buildTitle(): String {
    return if (name != null) {
        if (issueNumber == null) {
            name
        } else {
            stringResource(
                R.string.issue_title_template_without_name,
                name,
                issueNumber,
            )
        }
    } else if (issueNumber != null) {
        stringResource(
            R.string.issue_number_template,
            issueNumber
        )
    } else {
        stringResource(R.string.no_name_placeholder)
    }
}