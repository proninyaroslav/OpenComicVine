package org.proninyaroslav.opencomicvine.ui.components.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    fallbackImageUrl: String? = null,
    imageDescription: String?,
    @DrawableRes placeholder: Int,
    imageScale: ContentScale = ContentScale.Crop,
    imageForceStretchHeight: Boolean = true,
    imageAspectRatio: Float? = null,
    onClick: () -> Unit,
    onImageWidthChanged: (Dp) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    var imageWidth by remember { mutableStateOf(120.dp) }

    Card(
        modifier = modifier
            .width(imageWidth)
            .wrapContentHeight(Alignment.Top),
        onClick = onClick,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            ImageCard(
                imageUrl = imageUrl,
                fallbackImageUrl = fallbackImageUrl,
                imageDescription = imageDescription,
                imageWidth = imageWidth,
                onImageWidthChanged = {
                    imageWidth = it
                    onImageWidthChanged(it)
                },
                placeholder = placeholder,
                imageScale = imageScale,
                imageForceStretchHeight = imageForceStretchHeight,
                imageAspectRatio = imageAspectRatio,
            )
            content()
        }
    }
}

@Preview
@Composable
private fun PreviewCardWithImage() {
    OpenComicVineTheme {
        val titleStyle = MaterialTheme.typography.titleMedium
        LazyColumn {
            item {
                CardWithImage(
                    imageUrl = "https://dummyimage.com/320",
                    imageDescription = "Dummy image",
                    placeholder = R.drawable.placeholder_square,
                    onClick = {},
                ) {
                    Text(
                        "Title",
                        style = titleStyle,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewCardWithImage_Loading() {
    OpenComicVineTheme {
        val titleStyle = MaterialTheme.typography.titleMedium
        LazyColumn {
            item {
                CardWithImage(
                    imageUrl = null,
                    imageDescription = "Dummy image",
                    placeholder = R.drawable.placeholder_square,
                    onClick = {},
                ) {
                    Text(
                        "",
                        style = titleStyle,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .defaultPlaceholder(visible = true),
                    )
                }
            }
        }
    }
}