package org.proninyaroslav.opencomicvine.ui.details

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.components.defaultPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun DetailsSummaryText(
    modifier: Modifier = Modifier,
    text: String?,
    @DrawableRes icon: Int? = null,
    maxLines: Int = Int.MAX_VALUE,
    isExpandedWidth: Boolean,
) {
    val style = LocalTextStyle.current
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .defaultPlaceholder(
                visible = text == null,
            )
            .then(
                if (text == null) {
                    Modifier.fillMaxWidth(if (isExpandedWidth) 0.25f else 1f)
                } else {
                    Modifier
                }
            ),
    ) {
        val iconSize = with(LocalDensity.current) { style.fontSize.toDp() }
        icon?.let {
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .paddingFromBaseline(top = iconSize / 6)
                    .size(iconSize)
            )
        }
        Text(
            text ?: "",
            maxLines = maxLines,
            style = style,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewDetailsSummaryText() {
    OpenComicVineTheme {
        DetailsSummaryText(
            text = "Text",
            icon = R.drawable.ic_calendar_month_24,
            isExpandedWidth = false,
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PreviewDetailsSummaryText_Loading() {
    OpenComicVineTheme {
        DetailsSummaryText(
            text = null,
            icon = R.drawable.ic_calendar_month_24,
            isExpandedWidth = false,
        )
    }
}

@Preview(
    name = "Expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewDetailsSummaryText_ExpandedWidth() {
    OpenComicVineTheme {
        DetailsSummaryText(
            text = "Text",
            icon = R.drawable.ic_calendar_month_24,
            isExpandedWidth = true,
        )
    }
}

@Preview(
    name = "Loading with expanded width",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
@Composable
private fun PreviewDetailsSummaryText_LoadingWithExpandedWidth() {
    OpenComicVineTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            DetailsSummaryText(
                text = null,
                icon = R.drawable.ic_calendar_month_24,
                isExpandedWidth = true,
            )
        }
    }
}