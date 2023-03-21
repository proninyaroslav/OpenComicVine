package org.proninyaroslav.opencomicvine.ui.components.error

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
fun HttpErrorPage(
    modifier: Modifier = Modifier,
    httpCode: Int,
    onRetry: () -> Unit,
    compact: Boolean = false
) {
    ErrorPageScaffold(
        {
            ErrorHeadline(
                text = stringResource(R.string.http_error_template, httpCode),
                icon = R.drawable.ic_public_24,
                compact = compact,
            )
        },
        bottomAction = {
            if (compact) {
                ErrorRefreshOutlinedButton(onClick = onRetry)
            } else {
                ErrorRefreshButton(onClick = onRetry)
            }
        },
        contentInCard = compact,
        modifier = modifier,
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHttpErrorPage() {
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            HttpErrorPage(
                httpCode = 404,
                onRetry = {},
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHttpErrorPageDark() {
    OpenComicVineTheme {
        Surface {
            Box(
                contentAlignment = Alignment.Center
            ) {
                HttpErrorPage(
                    httpCode = 404,
                    onRetry = {},
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHttpErrorPage_Compact() {
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            HttpErrorPage(
                httpCode = 404,
                onRetry = {},
                compact = true,
            )
        }
    }
}