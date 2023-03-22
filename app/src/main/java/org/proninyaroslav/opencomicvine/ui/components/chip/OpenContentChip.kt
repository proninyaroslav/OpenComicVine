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

package org.proninyaroslav.opencomicvine.ui.components.chip

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.inverse
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun OpenContentChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val direction = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides direction.inverse()) {
        SuggestionChip(
            label = {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Text(label, modifier = Modifier.padding(vertical = 4.dp))
                }
            },
            icon = {
                Icon(
                    painterResource(
                        if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
                            R.drawable.ic_chevron_left_24
                        } else {
                            R.drawable.ic_chevron_right_24
                        }
                    ),
                    contentDescription = stringResource(R.string.open),
                )
            },
            onClick = onClick,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
fun PreviewOpenContentChip() {
    OpenComicVineTheme {
        OpenContentChip(
            label = "Label",
            onClick = {},
        )
    }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOpenContentChip_Dark() {
    OpenComicVineTheme {
        Surface {
            OpenContentChip(
                label = "Label",
                onClick = {},
            )
        }
    }
}
