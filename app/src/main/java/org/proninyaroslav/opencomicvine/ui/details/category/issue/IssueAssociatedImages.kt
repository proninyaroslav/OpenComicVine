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

package org.proninyaroslav.opencomicvine.ui.details.category.issue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import org.proninyaroslav.opencomicvine.types.IssueDetails
import org.proninyaroslav.opencomicvine.model.isEven
import org.proninyaroslav.opencomicvine.ui.details.DetailsImage
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IssueAssociatedImages(
    images: List<IssueDetails.AssociatedImage>?,
    loading: Boolean,
    isExpandedWidth: Boolean,
    onClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        if (loading) {
            repeat(3) {
                ImageCard(
                    image = null,
                    isExpandedWidth = isExpandedWidth,
                    onClick = onClick,
                )
            }
        } else {
            images?.onEach {
                ImageCard(
                    image = it,
                    isExpandedWidth = isExpandedWidth,
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
private fun ImageCard(
    image: IssueDetails.AssociatedImage?,
    isExpandedWidth: Boolean,
    onClick: (url: String) -> Unit
) {
    image?.run {
        Card {
            DetailsImage(
                imageUrl = originalUrl,
                imageDescription = caption,
                isExpandedWidth = isExpandedWidth,
                onClick = onClick
            )
            caption?.let {
                Text(
                    caption,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    } ?: DetailsImage(
        imageUrl = null,
        imageDescription = null,
        isExpandedWidth = isExpandedWidth,
        onClick = onClick
    )
}

@Preview
@Composable
private fun PreviewIssueAssociatedImages() {
    OpenComicVineTheme {
        IssueAssociatedImages(
            images = List(3) {
                IssueDetails.AssociatedImage(
                    id = it,
                    originalUrl = "https://example.org",
                    caption = if (it.isEven()) "Caption $it" else null,
                    imageTags = "All images",
                )
            },
            loading = false,
            isExpandedWidth = false,
            onClick = {},
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewIssueAssociatedImages_Loading() {
    OpenComicVineTheme {
        IssueAssociatedImages(
            images = emptyList(),
            loading = true,
            isExpandedWidth = false,
            onClick = {},
        )
    }
}
