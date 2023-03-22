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

package org.proninyaroslav.opencomicvine.ui.components.error

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.components.OutlinedRetryButton
import org.proninyaroslav.opencomicvine.ui.components.RetryButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun ErrorRefreshButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RetryButton(
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun ErrorRefreshOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.error
    OutlinedRetryButton(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(color)),
        onClick = onClick,
        modifier = modifier,
    )
}

@Preview
@Composable
fun PreviewErrorRefreshButton() {
    OpenComicVineTheme {
        ErrorRefreshButton(onClick = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewErrorRefreshButtonDark() {
    OpenComicVineTheme {
        ErrorRefreshButton(onClick = {})
    }
}

@Preview
@Composable
fun PreviewErrorRefreshOutlinedButton() {
    OpenComicVineTheme {
        ErrorRefreshOutlinedButton(onClick = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewErrorRefreshOutlinedButtonDark() {
    OpenComicVineTheme {
        Surface {
            ErrorRefreshOutlinedButton(onClick = {})
        }
    }
}
