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

package org.proninyaroslav.opencomicvine.ui.details

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.model.isOdd
import org.proninyaroslav.opencomicvine.ui.components.ResponsiveText
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.isMultiWord
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Immutable
data class DetailsGeneralInfoItem(
    val label: String,
    @DrawableRes val icon: Int,
    val content: @Composable () -> Unit,
)

@Composable
fun DetailsGeneralInfo(
    items: List<DetailsGeneralInfoItem>,
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    DetailsExpandableCard(
        title = stringResource(R.string.details_general_information),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        expanded = expanded,
        onHeaderClick = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            if (loading) {
                LoadingPlaceholder()
            } else if (items.isEmpty()) {
                NoInfoPlaceholder()
            } else {
                items.onEachIndexed { index, it ->
                    Item(
                        item = it,
                        index = index,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoInfoPlaceholder() {
    EmptyListPlaceholder(
        icon = R.drawable.ic_info_outline_24,
        label = stringResource(R.string.no_info),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}

@Composable
private fun LoadingPlaceholder() {
    PlaceholderItem(Modifier.fillMaxWidth(0.8f))
    PlaceholderItem(Modifier.fillMaxWidth())
    PlaceholderItem(Modifier.fillMaxWidth(0.95f))
}

@Composable
private fun PlaceholderItem(
    modifier: Modifier = Modifier,
) {
    Text(
        "",
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .defaultPlaceholder(visible = true)
    )
}

@Composable
private fun Item(
    item: DetailsGeneralInfoItem,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val (containerColor, contentColor) = if (index.isOdd()) {
        colorScheme.run { secondaryContainer to onSecondaryContainer }
    } else {
        colorScheme.run {
            primaryContainer.copy(alpha = 0.25f) to onSecondaryContainer
        }
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        item.run {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.background(containerColor),
            ) {
                ItemLabel(
                    label = label,
                    icon = icon,
                    modifier = Modifier.weight(2f)
                )
                ItemContent(
                    content = content,
                    modifier = Modifier.weight(3f)
                )
            }
        }
    }
}

@Composable
private fun ItemContent(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = modifier.padding(16.dp),
        ) {
            SelectionContainer {
                content()
            }
        }
    }
}

@Composable
private fun ItemLabel(
    label: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
) {
    val lines by remember(label) {
        derivedStateOf {
            if (label.isMultiWord()) 2 else 1
        }
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp)
    ) {
        val style = MaterialTheme.typography.bodyLarge
            .copy(fontWeight = FontWeight.Medium)
        val iconSize = 20.dp
        Icon(
            painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .paddingFromBaseline(top = 1.dp)
                .size(iconSize)
        )
        ResponsiveText(
            label,
            style = style,
            maxLines = lines,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewDetailsGeneralInfo() {
    OpenComicVineTheme {
        DetailsGeneralInfo(
            items = listOf(
                DetailsGeneralInfoItem(
                    label = "Item 1",
                    icon = R.drawable.ic_face_24,
                    content = { Text("Content 1") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 2",
                    icon = R.drawable.ic_favorite_24,
                    content = { Text("Content 2") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 3",
                    icon = R.drawable.ic_menu_book_24,
                    content = { Text("Content 3") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 4",
                    icon = R.drawable.ic_library_books_24,
                    content = { Text("Content 4") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 5",
                    icon = R.drawable.ic_public_24,
                    content = { Text("Content 5") },
                )
            ),
            loading = false,
        )
    }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDetailsGeneralInfo_Dark() {
    OpenComicVineTheme {
        DetailsGeneralInfo(
            items = listOf(
                DetailsGeneralInfoItem(
                    label = "Item 1",
                    icon = R.drawable.ic_face_24,
                    content = { Text("Content 1") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 2",
                    icon = R.drawable.ic_favorite_24,
                    content = { Text("Content 2") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 3",
                    icon = R.drawable.ic_menu_book_24,
                    content = { Text("Content 3") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 4",
                    icon = R.drawable.ic_library_books_24,
                    content = { Text("Content 4") },
                ),
                DetailsGeneralInfoItem(
                    label = "Item 5",
                    icon = R.drawable.ic_public_24,
                    content = { Text("Content 5") },
                )
            ),
            loading = false,
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsGeneralInfo_Loading() {
    OpenComicVineTheme {
        DetailsGeneralInfo(
            items = emptyList(),
            loading = true,
        )
    }
}

@Preview(name = "Empty")
@Composable
private fun PreviewDetailsGeneralInfo_Empty() {
    OpenComicVineTheme {
        DetailsGeneralInfo(
            items = emptyList(),
            loading = false,
        )
    }
}

@Preview(name = "Empty", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDetailsGeneralInfo_EmptyDark() {
    OpenComicVineTheme {
        DetailsGeneralInfo(
            items = emptyList(),
            loading = false,
        )
    }
}

@Preview(name = "Text wrapping")
@Composable
private fun PreviewDetailsGeneralInfo_TextWrapping() {
    OpenComicVineTheme {
        DetailsGeneralInfo(
            items = listOf(
                DetailsGeneralInfoItem(
                    label = "Superheroes name",
                    icon = R.drawable.ic_face_24,
                    content = { Text("Content") },
                ),
            ),
            loading = false,
            modifier = Modifier.width(250.dp),
        )
    }
}
