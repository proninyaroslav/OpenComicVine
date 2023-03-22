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

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.applyTonalElevation
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painterResource(R.drawable.ic_arrow_back_24),
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
fun FilledTonalBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.applyTonalElevation(
                backgroundColor = MaterialTheme.colorScheme.surface,
                elevation = 3.0.dp,
            ),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier,
    ) {
        Icon(
            painterResource(R.drawable.ic_arrow_back_24),
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Preview
@Composable
private fun PreviewBackButton() {
    OpenComicVineTheme {
        BackButton(onClick = {})
    }
}

@Preview(name = "Filled Tonal")
@Composable
private fun PreviewBackButton_FilledTonal() {
    OpenComicVineTheme {
        FilledTonalBackButton(onClick = {})
    }
}
