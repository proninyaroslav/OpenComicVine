package org.proninyaroslav.opencomicvine.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.paging.LoadState
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.paging.SearchSource
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.ComicVineResultErrorView
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage

@Composable
fun SearchErrorView(
    state: LoadState.Error,
    toMediatorError: (LoadState.Error) -> SearchSource.Error?,
    formatFetchErrorMessage: (errorMessage: String) -> String,
    compact: Boolean,
    onRetry: () -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onCopyStackTrace = fun(stackTrace: String) {
        clipboardManager.setText(AnnotatedString(stackTrace))
        coroutineScope.launch {
            snackbarState.showSnackbar(
                context.getString(R.string.copied_to_clipboard),
            )
        }
    }

    toMediatorError(state)?.let { error ->
        when (error) {
            is SearchSource.Error.Service -> {
                ComicVineResultErrorView(
                    errorMessage = error.errorMessage,
                    statusCode = error.statusCode,
                    compact = compact,
                    onReport = {
                        onReport(
                            ErrorReportInfo(
                                Exception("${error.errorMessage}, ${error.statusCode}"),
                            )
                        )
                    },
                    modifier = modifier,
                )
            }
            is SearchSource.Error.Fetching -> {
                ComicVineResultErrorView(
                    error = error.error,
                    formatFetchErrorMessage = formatFetchErrorMessage,
                    onRetry = onRetry,
                    onReport = onReport,
                    compact = compact,
                    modifier = modifier,
                )
            }
        }
    } ?: state.error.run {
        FatalErrorPage(
            errorMessage = formatFetchErrorMessage(toString()),
            stackTrace = stackTraceToString(),
            onReport = { onReport(ErrorReportInfo(this)) },
            compact = compact,
            onCopyStackTrace = onCopyStackTrace,
            modifier = modifier,
        )
    }
}