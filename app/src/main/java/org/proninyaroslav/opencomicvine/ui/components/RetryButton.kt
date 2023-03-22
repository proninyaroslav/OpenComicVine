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

package org.proninyaroslav.opencomicvine.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun RetryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
) {
    FilledTonalButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier,
    ) {
        ButtonContent()
    }
}

@Composable
fun OutlinedRetryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder,
) {
    OutlinedButton(
        onClick = onClick,
        colors = colors,
        border = border,
        modifier = modifier,
    ) {
        ButtonContent()
    }
}

@Composable
private fun ButtonContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(R.drawable.ic_refresh_24),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(18.dp)
        )
        Text(stringResource(R.string.retry))
    }
}

@Preview
@Composable
private fun PreviewRefreshButton() {
    OpenComicVineTheme {
        RetryButton(onClick = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewRefreshButtonDark() {
    OpenComicVineTheme {
        RetryButton(onClick = {})
    }
}

@Preview
@Composable
fun PreviewOutlinedRefreshButton() {
    OpenComicVineTheme {
        RetryButton(onClick = {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewOutlinedRefreshButtonDark() {
    OpenComicVineTheme {
        Surface {
            RetryButton(onClick = {})
        }
    }
}
