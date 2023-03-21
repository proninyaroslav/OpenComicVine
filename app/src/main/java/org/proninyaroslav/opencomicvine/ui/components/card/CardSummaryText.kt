package org.proninyaroslav.opencomicvine.ui.components.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun CardSummaryText(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes icon: Int? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    val style = MaterialTheme.typography.bodySmall
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier,
    ) {
        icon?.let {
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(16.dp)
            )
        }
        Text(
            text,
            maxLines = maxLines,
            style = style,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
fun PreviewCardSummaryText() {
    OpenComicVineTheme {
        CardSummaryText(
            text = "Summary",
            icon = R.drawable.ic_calendar_month_24,
        )
    }
}

@Preview(name = "Long text")
@Composable
fun PreviewCardSummaryText_LongText() {
    OpenComicVineTheme {
        CardSummaryText(
            text = "Long Long Long Summary",
            icon = R.drawable.ic_calendar_month_24,
            modifier = Modifier.width(100.dp),
        )
    }
}