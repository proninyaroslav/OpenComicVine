package org.proninyaroslav.opencomicvine.ui.components.card

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

private const val TAG = "ImageCard"

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    fallbackImageUrl: String? = null,
    imageDescription: String?,
    imageWidth: Dp,
    onImageWidthChanged: (Dp) -> Unit,
    @DrawableRes placeholder: Int,
    imageScale: ContentScale = ContentScale.Crop,
    imageForceStretchHeight: Boolean = true,
    imageAspectRatio: Float? = null,
) {
    val density = LocalDensity.current

    var placeholderHeight by remember { mutableStateOf(imageWidth) }
    var showImageBorder by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf(imageUrl) }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        border = if (showImageBorder) {
            CardDefaults.outlinedCardBorder()
        } else {
            BorderStroke(0.dp, Color.Transparent)
        },
        modifier = modifier,
    ) {
        val imageModifier = Modifier
            .onGloballyPositioned {
                val newWidth = with(density) { it.size.width.toDp() }
                if (newWidth != imageWidth) {
                    onImageWidthChanged(imageWidth)
                }
            }
            .fillMaxWidth()
            .then(
                imageAspectRatio?.let {
                    Modifier.aspectRatio(imageAspectRatio)
                } ?: Modifier
            )
        if (url == null) {
            Placeholder(
                image = placeholder,
                contentDescription = imageDescription,
                imageAspectRatio = imageAspectRatio,
                isHighlightVisible = true,
                modifier = imageModifier.wrapContentHeight(),
            )
        } else {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
            )
            when (val s = painter.state) {
                is AsyncImagePainter.State.Error -> {
                    Log.d(TAG, "Unable to load image $imageUrl", s.result.throwable)
                    fallbackImageUrl?.let {
                        if (url != fallbackImageUrl) {
                            url = fallbackImageUrl
                        }
                    }
                }
                is AsyncImagePainter.State.Success -> {
                    showImageBorder = true
                }
                else -> {}
            }

            Box {
                Image(
                    painter = painter,
                    contentDescription = imageDescription,
                    alignment = Alignment.Center,
                    contentScale = imageScale,
                    modifier = imageModifier
                        .then(
                            if (painter.state !is AsyncImagePainter.State.Success) {
                                // Limit infinity height
                                Modifier.height(placeholderHeight)
                            } else if (imageForceStretchHeight) {
                                Modifier.fillMaxHeight()
                            } else {
                                Modifier
                            }
                        ),
                )
                if (painter.state !is AsyncImagePainter.State.Success) {
                    Placeholder(
                        image = placeholder,
                        contentDescription = imageDescription,
                        imageAspectRatio = imageAspectRatio,
                        isHighlightVisible = painter.state !is AsyncImagePainter.State.Error,
                        modifier = Modifier
                            .onGloballyPositioned {
                                val newHeight = with(density) { it.size.height.toDp() }
                                if (newHeight != placeholderHeight) {
                                    placeholderHeight = newHeight
                                }
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun Placeholder(
    @DrawableRes image: Int,
    contentDescription: String?,
    imageAspectRatio: Float?,
    isHighlightVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(image),
        contentDescription = contentDescription,
        modifier = modifier
            .placeholder(
                visible = isHighlightVisible,
                color = Color.Transparent,
                highlight = PlaceholderHighlight.fade()
            )
            .fillMaxWidth()
            .then(
                imageAspectRatio?.let {
                    Modifier.aspectRatio(imageAspectRatio)
                } ?: Modifier
            )
    )
}

@Preview
@Composable
private fun PreviewImageCard() {
    OpenComicVineTheme {
        var imageWidth by remember { mutableStateOf(120.dp) }
        LazyColumn {
            item {
                ImageCard(
                    imageUrl = "https://dummyimage.com/320",
                    imageDescription = "Dummy image",
                    imageWidth = imageWidth,
                    onImageWidthChanged = { imageWidth = it },
                    placeholder = R.drawable.placeholder_square,
                )
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewImageCard_Loading() {
    OpenComicVineTheme {
        var imageWidth by remember { mutableStateOf(120.dp) }
        LazyColumn {
            item {
                ImageCard(
                    imageUrl = null,
                    imageDescription = "Dummy image",
                    imageWidth = imageWidth,
                    onImageWidthChanged = { imageWidth = it },
                    placeholder = R.drawable.placeholder_square,
                )
            }
        }
    }
}