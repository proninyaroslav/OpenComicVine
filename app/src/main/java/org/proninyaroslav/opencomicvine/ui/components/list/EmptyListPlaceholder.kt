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

package org.proninyaroslav.opencomicvine.ui.components.list

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun EmptyListPlaceholder(
    @DrawableRes icon: Int,
    label: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    if (compact) {
        ElevatedCard(
            modifier = modifier
                .defaultMinSize(minHeight = 50.dp)
                // Limit the width for large screens
                .wrapContentWidth(align = Alignment.Start)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .widthIn(max = 550.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(18.dp),
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    label,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    } else {
        val tint = MaterialTheme.colorScheme.surfaceTint
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(88.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.headlineMedium.copy(color = tint),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewCharactersEmptyList() {
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            EmptyListPlaceholder(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.no_characters),
            )
        }
    }
}

@Preview(showSystemUi = true, name = "Compact")
@Composable
fun PreviewCharactersEmptyList_Compact() {
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            EmptyListPlaceholder(
                icon = R.drawable.ic_face_24,
                label = stringResource(R.string.no_characters),
                compact = true,
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewCharactersEmptyListDark() {
    OpenComicVineTheme {
        Surface {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                EmptyListPlaceholder(
                    icon = R.drawable.ic_face_24,
                    label = stringResource(R.string.no_characters),
                )
            }
        }
    }
}
