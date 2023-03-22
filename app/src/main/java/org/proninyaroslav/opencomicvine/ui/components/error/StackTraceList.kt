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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.ExpandableOutlinedCard
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@Composable
fun StackTraceList(
    modifier: Modifier = Modifier,
    stackTrace: String,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    onCopyStackTrace: (String) -> Unit,
    showBorder: Boolean = true,
) {
    val color = MaterialTheme.colorScheme.onErrorContainer
    val defaultBorder = CardDefaults.outlinedCardBorder()

    ExpandableOutlinedCard(
        title = {
            Text(
                stringResource(R.string.stack_trace),
                style = MaterialTheme.typography.titleSmall,
            )
        },
        expanded = expanded,
        onHeaderClick = onHeaderClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = color
        ),
        border = if (showBorder) {
            defaultBorder.copy(brush = SolidColor(color))
        } else {
            defaultBorder.copy(brush = SolidColor(Color.Transparent))
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            CopyToClipboardButton(
                onClick = { onCopyStackTrace(stackTrace) },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 8.dp),
            )
            SelectionContainer {
                Text(
                    stackTrace,
                    style = MaterialTheme.typography.bodyMedium
                        .copy(fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}

@Composable
private fun CopyToClipboardButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = LocalContentColor.current,
        ),
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        modifier = modifier,
    ) {
        Icon(
            painterResource(R.drawable.ic_content_copy_24),
            contentDescription = stringResource(R.string.copy_to_clipboard),
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(R.string.copy_to_clipboard))
    }
}

@Preview
@Composable
fun PreviewStackTraceList() {
    OpenComicVineTheme {
        StackTraceList(
            stackTrace = IOException().stackTraceToString(),
            expanded = false,
            onHeaderClick = {},
            onCopyStackTrace = {},
        )
    }
}

@Preview
@Composable
fun PreviewStackTraceList_NoBorder() {
    OpenComicVineTheme {
        StackTraceList(
            stackTrace = IOException().stackTraceToString(),
            expanded = false,
            showBorder = false,
            onHeaderClick = {},
            onCopyStackTrace = {},
        )
    }
}

@Preview
@Composable
fun PreviewStackTraceList_Expanded() {
    OpenComicVineTheme {
        StackTraceList(
            stackTrace = IOException().stackTraceToString(),
            expanded = true,
            onHeaderClick = {},
            onCopyStackTrace = {},
        )
    }
}
