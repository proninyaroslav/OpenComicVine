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

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Immutable
data class HyperlinkTextItem(
    val url: String,
    val text: String? = null,
)

@Composable
fun HyperlinkText(
    text: String,
    items: List<HyperlinkTextItem>,
    modifier: Modifier = Modifier,
    linkTextColor: Color = MaterialTheme.colorScheme.primary,
    linkTextFontWeight: FontWeight = FontWeight.Medium,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    linkFontSize: TextUnit = TextUnit.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val annotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = style.toSpanStyle().copy(color = LocalContentColor.current),
            start = 0,
            end = text.length
        )
        items.forEach { item ->
            val itemText = item.text ?: item.url
            val startIndex = text.indexOf(itemText)
            val endIndex = startIndex + itemText.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    fontSize = linkFontSize,
                    fontWeight = linkTextFontWeight,
                    textDecoration = linkTextDecoration
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = item.url,
                start = startIndex,
                end = endIndex
            )
        }
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = style,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}

@Preview
@Composable
fun PreviewHyperlinkText() {
    OpenComicVineTheme {
        HyperlinkText(
            text = "Text https://comicvine.gamespot.com",
            items = listOf(
                HyperlinkTextItem(
                    text = "https://comicvine.gamespot.com",
                    url = "https://comicvine.gamespot.com",
                )
            ),
        )
    }
}
