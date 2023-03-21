package org.proninyaroslav.opencomicvine.ui.components.error

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@Composable
fun ErrorHeadline(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes icon: Int = R.drawable.ic_error_24,
    compact: Boolean = false,
) {
    val textColor = if (compact) {
        LocalContentColor.current
    } else {
        MaterialTheme.colorScheme.error
    }
    val textStyle = MaterialTheme.typography.run {
        if (compact) titleMedium else headlineSmall
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(36.dp)
        )
        Text(
            text = text,
            style = textStyle.copy(color = textColor),
            modifier = Modifier.padding(
                top = if (compact) 5.dp else 0.dp
            ),
        )
    }
}

@Preview("Short")
@Composable
fun PreviewErrorText_Short() {
    OpenComicVineTheme {
        ErrorHeadline(
            text = "Short"
        )
    }
}

@Preview("Long")
@Composable
fun PreviewErrorText_Long() {
    OpenComicVineTheme {
        ErrorHeadline(
            text = "${IOException("Very long error text")}"
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewErrorTextDark() {
    OpenComicVineTheme {
        Surface {
            ErrorHeadline(
                text = "${IOException("Very long error text")}"
            )
        }
    }
}

@Preview("Compact")
@Composable
fun PreviewErrorText_Compact() {
    OpenComicVineTheme {
        ErrorHeadline(
            text = "${IOException("Very long error text")}",
            compact = true,
        )
    }
}