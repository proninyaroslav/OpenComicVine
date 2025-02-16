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

import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun DetailsPlaceholderText(
    text: String?,
    visible: Boolean,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultPlaceholder(
                visible = visible,
            )
    ) {
        Text(
            text ?: "",
            modifier = modifier
                .then(
                    if (visible) {
                        Modifier.fillMaxWidth(if (isExpandedWidth) 0.25f else 1f)
                    } else {
                        Modifier
                    }
                ),
        )
    }
}

@Preview
@Composable
private fun PreviewDetailsPlaceholderText() {
    OpenComicVineTheme {
        DetailsPlaceholderText(
            text = "Text",
            visible = false,
            isExpandedWidth = false,
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsPlaceholderText_Loading() {
    OpenComicVineTheme {
        DetailsPlaceholderText(
            text = "Text",
            visible = true,
            isExpandedWidth = false,
        )
    }
}

@Preview(
    name = "Expanded width",
    uiMode = UI_MODE_TYPE_NORMAL,
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
private fun PreviewDetailsPlaceholderText_ExpandedWidth() {
    OpenComicVineTheme {
        DetailsPlaceholderText(
            text = "Text",
            visible = false,
            isExpandedWidth = true,
        )
    }
}

@Preview(
    name = "Loading with expanded width",
    uiMode = UI_MODE_TYPE_NORMAL,
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Composable
private fun PreviewDetailsPlaceholderText_LoadingWithExpandedWidth() {
    OpenComicVineTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            DetailsPlaceholderText(
                text = "Test",
                visible = true,
                isExpandedWidth = true,
            )
        }
    }
}
