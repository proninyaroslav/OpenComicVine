package org.proninyaroslav.opencomicvine.ui.components.error

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@Composable
fun FatalErrorPage(
    modifier: Modifier = Modifier,
    errorMessage: String,
    stackTrace: String? = null,
    showReportButton: Boolean = true,
    compact: Boolean = false,
    onReport: (() -> Unit)? = null,
    onCopyStackTrace: (String) -> Unit,
) {
    var stackTraceExpanded by rememberSaveable { mutableStateOf(false) }

    ErrorPageScaffold(
        { ErrorHeadline(text = errorMessage, compact = compact) },
        {
            stackTrace?.let {
                StackTraceList(
                    stackTrace = stackTrace,
                    expanded = stackTraceExpanded,
                    showBorder = !compact,
                    onHeaderClick = { stackTraceExpanded = !stackTraceExpanded },
                    onCopyStackTrace = onCopyStackTrace,
                )
            }
        },
        bottomAction = {
            if (showReportButton) {
                if (compact) {
                    ErrorReportOutlinedButton(onClick = onReport ?: {})
                } else {
                    ErrorReportButton(onClick = onReport ?: {})
                }
            }
        },
        contentInCard = compact,
        modifier = modifier,
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFatalErrorPage() {
    val error = IOException("Very long error text")
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            FatalErrorPage(
                errorMessage = "$error",
                stackTrace = error.stackTraceToString(),
                showReportButton = true,
                onCopyStackTrace = {},
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFatalErrorPageDark() {
    val error = IOException("Very long error text")
    OpenComicVineTheme {
        Surface {
            Box(
                contentAlignment = Alignment.Center
            ) {
                FatalErrorPage(
                    errorMessage = "$error",
                    stackTrace = error.stackTraceToString(),
                    showReportButton = true,
                    onCopyStackTrace = {},
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFatalErrorPage_NoReportButton() {
    val error = IOException("Very long error text")
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            FatalErrorPage(
                errorMessage = "$error",
                stackTrace = error.stackTraceToString(),
                showReportButton = false,
                onCopyStackTrace = {},
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFatalErrorPage_Compact() {
    val error = IOException("Very long error text")
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            FatalErrorPage(
                errorMessage = "$error",
                stackTrace = error.stackTraceToString(),
                compact = true,
                onCopyStackTrace = {},
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFatalErrorPage_NoStackTrace() {
    val error = IOException("Very long error text")
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            FatalErrorPage(
                errorMessage = "$error",
                showReportButton = true,
                onCopyStackTrace = {},
            )
        }
    }
}