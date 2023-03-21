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
import org.proninyaroslav.opencomicvine.data.LocationInfo
import org.proninyaroslav.opencomicvine.ui.calculateTextHeight
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun LocationCard(
    locationInfo: LocationInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: @Composable () -> Unit = {},
) {
    InnerLocationCard(
        name = locationInfo?.name ?: "",
        imageUrl = locationInfo?.image?.squareMedium,
        fallbackImageUrl = locationInfo?.image?.originalUrl,
        onClick = onClick,
        loading = locationInfo == null,
        additionalInfo = additionalInfo,
        modifier = modifier
    )
}

@Composable
private fun InnerLocationCard(
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
fun PreviewLocationCard() {
    OpenComicVineTheme {
        InnerLocationCard(
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
fun PreviewLocationCard_LongName() {
    OpenComicVineTheme {
        InnerLocationCard(
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
fun PreviewLocationCard_NameOverflow() {
    OpenComicVineTheme {
        InnerLocationCard(
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
fun PreviewLocationCard_Loading() {
    OpenComicVineTheme {
        InnerLocationCard(
            name = "",
            imageUrl = null,
            fallbackImageUrl = null,
            onClick = {},
            loading = true,
        )
    }
}