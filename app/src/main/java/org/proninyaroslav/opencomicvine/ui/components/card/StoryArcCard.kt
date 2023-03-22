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

package org.proninyaroslav.opencomicvine.ui.components.card

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.StoryArcInfo
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun StoryArcCard(
    storyArcInfo: StoryArcInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: @Composable () -> Unit = {},
) {
    InnerStoryArcCard(
        name = storyArcInfo?.name ?: "",
        imageUrl = storyArcInfo?.image?.squareMedium,
        fallbackImageUrl = storyArcInfo?.image?.originalUrl,
        onClick = onClick,
        loading = storyArcInfo == null,
        additionalInfo = additionalInfo,
        modifier = modifier
    )
}

@Composable
private fun InnerStoryArcCard(
    name: String,
    imageUrl: String?,
    fallbackImageUrl: String?,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: @Composable () -> Unit = {},
) {
    val titleStyle = MaterialTheme.typography.titleMedium
    val maxLines = 3
    CardWithImage(
        imageUrl = imageUrl,
        fallbackImageUrl = fallbackImageUrl,
        imageDescription = name,
        placeholder = R.drawable.placeholder_square,
        imageAspectRatio = 1f, // Square
        modifier = modifier,
        onClick = onClick,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            name,
            maxLines = maxLines,
            style = titleStyle,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(titleStyle.calculateTextHeight(maxLines = maxLines))
                .wrapContentHeight(align = Alignment.CenterVertically)
                .defaultPlaceholder(visible = loading)
                .then(
                    if (loading) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                    }
                ),
        )
        additionalInfo()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview
@Composable
fun PreviewStoryArcCard() {
    OpenComicVineTheme {
        InnerStoryArcCard(
            name = "Name",
            imageUrl = "https://dummyimage.com/320",
            fallbackImageUrl = "https://dummyimage.com/320",
            onClick = {},
            loading = false,
        )
    }
}

@Preview("Long name")
@Composable
fun PreviewStoryArcCard_LongName() {
    OpenComicVineTheme {
        InnerStoryArcCard(
            name = "Very very long name",
            imageUrl = "https://dummyimage.com/320",
            fallbackImageUrl = "https://dummyimage.com/320",
            onClick = {},
            loading = false
        )
    }
}

@Preview("Name overflow")
@Composable
fun PreviewStoryArcCard_NameOverflow() {
    OpenComicVineTheme {
        InnerStoryArcCard(
            name = "Very very very very long name",
            imageUrl = "https://dummyimage.com/320",
            fallbackImageUrl = "https://dummyimage.com/320",
            onClick = {},
            loading = false
        )
    }
}

@Preview(name = "Loading")
@Composable
fun PreviewStoryArcCard_Loading() {
    OpenComicVineTheme {
        InnerStoryArcCard(
            name = "",
            imageUrl = null,
            fallbackImageUrl = null,
            onClick = {},
            loading = true,
        )
    }
}
