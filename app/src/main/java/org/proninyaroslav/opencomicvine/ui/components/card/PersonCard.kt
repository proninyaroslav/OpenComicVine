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
import org.proninyaroslav.opencomicvine.data.PersonInfo
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun PersonCard(
    personInfo: PersonInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: @Composable () -> Unit = {},
) {
    InnerPersonCard(
        name = personInfo?.name ?: "",
        imageUrl = personInfo?.image?.squareMedium,
        fallbackImageUrl = personInfo?.image?.originalUrl,
        onClick = onClick,
        loading = personInfo == null,
        additionalInfo = additionalInfo,
        modifier = modifier
    )
}

@Composable
private fun InnerPersonCard(
    name: String,
    imageUrl: String?,
    fallbackImageUrl: String?,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: @Composable () -> Unit = {},
) {
    val titleStyle = MaterialTheme.typography.titleMedium
    val maxLines = 2
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
fun PreviewPersonCard() {
    OpenComicVineTheme {
        InnerPersonCard(
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
fun PreviewPersonCard_LongName() {
    OpenComicVineTheme {
        InnerPersonCard(
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
fun PreviewPersonCard_NameOverflow() {
    OpenComicVineTheme {
        InnerPersonCard(
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
fun PreviewPersonCard_Loading() {
    OpenComicVineTheme {
        InnerPersonCard(
            name = "",
            imageUrl = null,
            fallbackImageUrl = null,
            onClick = {},
            loading = true,
        )
    }
}
