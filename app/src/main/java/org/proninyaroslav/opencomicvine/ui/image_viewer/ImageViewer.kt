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

package org.proninyaroslav.opencomicvine.ui.image_viewer

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.smarttoolfactory.image.zoom.enhancedZoom
import com.smarttoolfactory.image.zoom.rememberEnhancedZoomState
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Immutable
data class ImageViewerState(
    var drawable: Drawable?,
)

@Composable
fun rememberImageViewerState() = remember {
    ImageViewerState(drawable = null)
}

@Composable
fun ImageViewer(
    modifier: Modifier = Modifier,
    imageState: ImageViewerState = rememberImageViewerState(),
    url: Uri,
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    val zoomState = rememberEnhancedZoomState(
        key1 = url,
        minZoom = 0.5f,
        imageSize = imageSize,
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            loading = {
                val state = painter.state
                if (state is AsyncImagePainter.State.Success) {
                    SubcomposeAsyncImageContent()
                } else if (state !is AsyncImagePainter.State.Error) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            },
            onError = {
                Log.d(url.toString(), "Unable to load image", it.result.throwable)
            },
            onSuccess = {
                imageState.drawable = it.result.drawable
            },
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .onSizeChanged { imageSize = it }
                .enhancedZoom(
                    enhancedZoomState = zoomState,
                ),
        )
    }
}

@Preview
@Composable
fun PreviewImageViewer() {
    OpenComicVineTheme {
        ImageViewer(
            url = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg".toUri(),
        )
    }
}
