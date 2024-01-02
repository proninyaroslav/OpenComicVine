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

package org.proninyaroslav.opencomicvine.ui.details.category.character

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.CharacterDetails
import org.proninyaroslav.opencomicvine.ui.components.chip.ChipFlowRow
import org.proninyaroslav.opencomicvine.ui.components.chip.OpenContentChip
import org.proninyaroslav.opencomicvine.ui.components.chip.TextChip
import org.proninyaroslav.opencomicvine.ui.details.DetailsGeneralInfo
import org.proninyaroslav.opencomicvine.ui.details.DetailsGeneralInfoItem
import org.proninyaroslav.opencomicvine.ui.details.DetailsPage
import org.proninyaroslav.opencomicvine.ui.toLocalizedName
import java.text.DateFormat

@Composable
fun CharacterGeneralInfo(
    details: CharacterDetails?,
    onCopyToClipboard: (String) -> Unit,
    onLoadPage: (DetailsPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailsGeneralInfo(
        items = details?.let {
            toInfoItems(
                details = details,
                onCopyToClipboard = onCopyToClipboard,
                onLoadPage = onLoadPage,
            )
        } ?: emptyList(),
        loading = details == null,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun toInfoItems(
    onCopyToClipboard: (String) -> Unit,
    onLoadPage: (DetailsPage) -> Unit,
    details: CharacterDetails,
): List<DetailsGeneralInfoItem> =
    details.run {
        listOfNotNull(
            DetailsGeneralInfoItem(
                label = stringResource(R.string.details_character_super_name),
                icon = R.drawable.ic_person_24,
                content = { Text(name) },
            ),
            realName?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_character_real_name),
                    icon = R.drawable.ic_person_24,
                    content = { Text(realName) },
                )
            },
            aliases?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_aliases),
                    icon = R.drawable.ic_person_24,
                    content = {
                        ChipFlowRow {
                            aliases.onEach {
                                TextChip(
                                    label = it,
                                    onClick = { onCopyToClipboard(it) },
                                )
                            }
                        }
                    },
                )
            },
            DetailsGeneralInfoItem(
                label = stringResource(R.string.details_character_gender),
                icon = R.drawable.ic_wc_24,
                content = { Text(gender.toLocalizedName()) },
            ),
            origin?.let {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_character_type_origin),
                    icon = R.drawable.ic_face_24,
                    content = { Text(origin.toLocalizedName()) },
                )
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
            if (creators.isNotEmpty()) {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_creators),
                    icon = R.drawable.ic_groups_24,
                    content = {
                        ChipFlowRow {
                            creators.onEach {
                                OpenContentChip(
                                    label = it.name,
                                    onClick = { onLoadPage(DetailsPage.Creator(it.id)) },
                                )
                            }
                        }
                    },
                )
            } else {
                null
            },
            firstAppearedInIssue?.run {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_character_first_appearance),
                    icon = R.drawable.ic_menu_book_24,
                    content = {
                        OpenContentChip(
                            label = buildTitle(),
                            onClick = { onLoadPage(DetailsPage.Issue(id)) },
                        )
                    },
                )
            },
            birth?.let {
                val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_birthday),
                    icon = R.drawable.ic_cake_24,
                    content = { Text(dateFormat.format(birth)) },
                )
            },
            if (issuesDiedIn.isNotEmpty()) {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_character_died_in),
                    icon = R.drawable.ic_skull_24,
                    content = {
                        ChipFlowRow {
                            issuesDiedIn.onEach {
                                OpenContentChip(
                                    label = it.name ?: stringResource(R.string.no_name_placeholder),
                                    onClick = { onLoadPage(DetailsPage.Issue(it.id)) },
                                )
                            }
                        }
                    },
                )
            } else {
                null
            },
            if (powers.isNotEmpty()) {
                DetailsGeneralInfoItem(
                    label = stringResource(R.string.details_character_powers),
                    icon = R.drawable.ic_bolt_24,
                    content = {
                        ChipFlowRow {
                            powers.onEach {
                                TextChip(
                                    label = it.name,
                                    onClick = { onCopyToClipboard(it.name) },
                                )
                            }
                        }
                    },
                )
            } else {
                null
            },
        )
    }

@Composable
fun CharacterDetails.FirstIssueAppearance.buildTitle(): String {
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
