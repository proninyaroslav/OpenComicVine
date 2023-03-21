package org.proninyaroslav.opencomicvine.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.proninyaroslav.opencomicvine.data.ImageInfo
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun DetailsHeader(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
    shortDescription: @Composable () -> Unit = {},
    shortInformation: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Row {
            image()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                shortInformation()
                Spacer(modifier = Modifier.height(16.dp))
                DetailsShortDescription {
                    shortDescription()
                }
            }
        }
    }
}

@Composable
private fun DetailsShortDescription(
    content: @Composable () -> Unit,
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
    ) {
        SelectionContainer {
            content()
        }
    }
}

@Preview
@Composable
private fun PreviewDetailsHeader() {
    val isExpandedWidth = false
    OpenComicVineTheme {
        DetailsHeader(
            image = {
                DetailsImage(
                    image = ImageInfo(
                        iconUrl = "",
                        mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                        screenUrl = "",
                        screenLargeUrl = "",
                        smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                        superUrl = "",
                        thumbUrl = "",
                        tinyUrl = "",
                        originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                        imageTags = null,
                    ),
                    imageDescription = null,
                    isExpandedWidth = isExpandedWidth,
                    onClick = {},
                )
            },
            shortDescription = { Text("Short description") },
            shortInformation = {
                Text("Short info 1")
                Text("Short info 2")
                Text("Short info 3")
            },
        )
    }
}

@Preview(
    name = "Expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewDetailsHeader_ExpandedWidth() {
    val isExpandedWidth = true
    OpenComicVineTheme {
        DetailsHeader(
            image = {
                DetailsImage(
                    image = ImageInfo(
                        iconUrl = "",
                        mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                        screenUrl = "",
                        screenLargeUrl = "",
                        smallUrl = "https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                        superUrl = "",
                        thumbUrl = "",
                        tinyUrl = "",
                        originalUrl = "https://comicvine.gamespot.com/a/uploads/original/12/124259/8126579-amazing_spider-man_vol_5_54_stormbreakers_variant_textless.jpg",
                        imageTags = null,
                    ),
                    imageDescription = null,
                    isExpandedWidth = isExpandedWidth,
                    onClick = {},
                )
            },
            shortDescription = { Text("Short description") },
            shortInformation = {
                Text("Short info 1")
                Text("Short info 2")
                Text("Short info 3")
            },
        )
    }
}