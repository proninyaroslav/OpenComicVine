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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ImageInfo
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.*
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun ComicVineBannerAppBar(
    modifier: Modifier = Modifier,
    title: String?,
    subtitle: String? = null,
    image: ImageInfo?,
    maxLines: Int = 3,
    scrollBehavior: TopAppBarWithImageScrollBehavior? = null,
    isExpandedWidth: Boolean,
    onBackButtonClicked: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBarWithImage(
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SelectionContainer {
                    Title(
                        name = title,
                        maxLines = maxLines,
                        isExpandedWidth = isExpandedWidth,
                    )
                }
                SelectionContainer {
                    subtitle?.let {
                        Subtitle(subtitle)
                    }
                }
            }
        },
        smallTitle = {
            Title(
                name = title,
                maxLines = 1,
                isExpandedWidth = isExpandedWidth,
            )
        },
        imageUrl = image?.screenLargeUrl,
        navigationIcon = { FilledTonalBackButton(onClick = onBackButtonClicked) },
        actions = actions,
        scrollBehavior = scrollBehavior,
        maxBottomPinnedHeight = TopAppBarWithImageTitleStyle.calculateTextHeight(
            maxLines = maxLines,
        ),
        modifier = modifier,
    )
}

@Composable
private fun Subtitle(
    subtitle: String
) {
    Text(
        subtitle,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
    )
}

@Composable
private fun Title(
    name: String?,
    maxLines: Int,
    isExpandedWidth: Boolean,
) {
    Text(
        name ?: "",
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .defaultPlaceholder(visible = name == null)
            .then(
                if (name == null) {
                    Modifier.fillMaxWidth(if (isExpandedWidth) 0.3f else 0.5f)
                } else {
                    Modifier
                }
            )
    )
}

@Preview
@Composable
private fun PreviewComicVineBannerTopBar() {
    OpenComicVineTheme {
        ComicVineBannerAppBar(
            title = "Name",
            subtitle = "Subtitle",
            image = ImageInfo(
                iconUrl = "",
                mediumUrl = "",
                screenUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                screenLargeUrl = "https://comicvine.gamespot.com/a/uploads/screen_kubrick/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                smallUrl = "",
                superUrl = "",
                thumbUrl = "",
                tinyUrl = "",
                originalUrl = "",
                imageTags = null,
            ),
            onBackButtonClicked = {},
            isExpandedWidth = false,
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewComicVineBannerTopBar_Loading() {
    OpenComicVineTheme {
        ComicVineBannerAppBar(
            title = null,
            image = null,
            onBackButtonClicked = {},
            isExpandedWidth = false,
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Very long name")
@Composable
private fun PreviewComicVineBannerTopBar_VeryLongName() {
    val scrollBehavior = rememberTopAppBarWithImageScrollBehavior()

    OpenComicVineTheme {
        Scaffold(
            topBar = {
                ComicVineBannerAppBar(
                    title = "Long long long long long long long name",
                    image = null,
                    onBackButtonClicked = {},
                    actions = {
                        FilledTonalActionButton(onClick = {}) {
                            Icon(
                                painterResource(R.drawable.ic_favorite_24),
                                contentDescription = null,
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    isExpandedWidth = false,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {}
    }
}

@Preview(
    name = "Loading and expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewComicVineBannerTopBar_LoadingAndExpandedWidth() {
    OpenComicVineTheme {
        ComicVineBannerAppBar(
            title = null,
            image = null,
            onBackButtonClicked = {},
            isExpandedWidth = true,
        )
    }
}
