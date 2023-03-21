package org.proninyaroslav.opencomicvine.ui.home

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
import org.proninyaroslav.opencomicvine.model.paging.recent.RecentEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.ComicVineResultErrorView
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage
import org.proninyaroslav.opencomicvine.ui.components.error.RetryableErrorPage

@Composable
fun RecentErrorView(
    state: LoadState.Error,
    toMediatorError: (LoadState.Error) -> RecentEntityRemoteMediator.Error?,
    formatFetchErrorMessage: (errorMessage: String) -> String,
    formatSaveErrorMessage: (errorMessage: String) -> String,
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
            is RecentEntityRemoteMediator.Error.Service -> {
                ComicVineResultErrorView(
                    errorMessage = error.errorMessage,
                    statusCode = error.statusCode,
                    compact = compact,
                    onReport = {
                        onReport(
                            ErrorReportInfo(
                                Exception("${error.errorMessage}, ${error.statusCode}")
                            )
                        )
                    },
                    modifier = modifier,
                )
            }
            is RecentEntityRemoteMediator.Error.Fetching -> {
                ComicVineResultErrorView(
                    error = error.error,
                    formatFetchErrorMessage = formatFetchErrorMessage,
                    onRetry = onRetry,
                    onReport = onReport,
                    compact = compact,
                    modifier = modifier,
                )
            }
            is RecentEntityRemoteMediator.Error.Save.IO -> RetryableErrorPage(
                errorMessage = formatSaveErrorMessage(error.exception.toString()),
                stackTrace = error.exception.stackTraceToString(),
                onRetry = onRetry,
                compact = compact,
                onCopyStackTrace = onCopyStackTrace,
                modifier = modifier,
            )
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