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

import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun ChipFlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: Dp = 8.dp,
    crossAxisSpacing: Dp = 4.dp,
    content: @Composable () -> Unit,
) {
    FlowRow(
        mainAxisSpacing = mainAxisSpacing,
        crossAxisSpacing = crossAxisSpacing,
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
fun PreviewChipFlowRow() {
    OpenComicVineTheme {
        ChipFlowRow {
            SuggestionChip(
                label = { Text("Chip 1") },
                onClick = {},
            )
            SuggestionChip(
                label = { Text("Chip 2") },
                onClick = {},
            )
            SuggestionChip(
                label = { Text("Chip 3") },
                onClick = {},
            )
        }
    }
}
