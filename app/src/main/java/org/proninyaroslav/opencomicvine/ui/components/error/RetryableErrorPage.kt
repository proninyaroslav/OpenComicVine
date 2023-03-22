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
fun RetryableErrorPage(
    modifier: Modifier = Modifier,
    errorMessage: String,
    stackTrace: String? = null,
    onRetry: () -> Unit,
    onCopyStackTrace: (String) -> Unit,
    compact: Boolean = false,
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
fun PreviewRetryableErrorPage() {
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            RetryableErrorPage(
                errorMessage = "Very long error text",
                onRetry = {},
                onCopyStackTrace = {},
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewRetryableErrorPageDark() {
    OpenComicVineTheme {
        Surface {
            Box(
                contentAlignment = Alignment.Center
            ) {
                RetryableErrorPage(
                    errorMessage = "Very long error text",
                    onRetry = {},
                    onCopyStackTrace = {},
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewRetryableErrorPage_Compact() {
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            RetryableErrorPage(
                errorMessage = "Very long error text",
                onRetry = {},
                compact = true,
                onCopyStackTrace = {},
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewRetryableErrorPage_CompactDark() {
    OpenComicVineTheme {
        Surface {
            Box(
                contentAlignment = Alignment.Center
            ) {
                RetryableErrorPage(
                    errorMessage = "Very long error text",
                    onRetry = {},
                    compact = true,
                    onCopyStackTrace = {},
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewRetryableErrorPage_WithStackTrace() {
    val error = IOException()
    OpenComicVineTheme {
        Box(
            contentAlignment = Alignment.Center
        ) {
            RetryableErrorPage(
                errorMessage = "$error",
                stackTrace = error.stackTraceToString(),
                onRetry = {},
                onCopyStackTrace = {},
            )
        }
    }
}
