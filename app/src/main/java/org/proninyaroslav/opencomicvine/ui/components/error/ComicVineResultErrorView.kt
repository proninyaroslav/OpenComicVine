package org.proninyaroslav.opencomicvine.ui.components.error

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.StatusCode
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage
import org.proninyaroslav.opencomicvine.ui.components.error.HttpErrorPage
import org.proninyaroslav.opencomicvine.ui.components.error.NetworkNotAvailable
import org.proninyaroslav.opencomicvine.ui.components.error.RetryableErrorPage

@Composable
fun ComicVineResultErrorView(
    error: ComicVineResult.Failed,
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

    when (error) {
        is ComicVineResult.Failed.ApiKeyError -> error.let {
            when (val apiError = it.error) {
                is ApiKeyRepository.GetResult.Failed.IO -> apiError.run {
                    RetryableErrorPage(
                        errorMessage = stringResource(
                            R.string.api_key_storage_error_template,
                            exception
                        ),
                        stackTrace = exception.stackTraceToString(),
                        onRetry = onRetry,
                        compact = compact,
                        onCopyStackTrace = onCopyStackTrace,
                        modifier = modifier,
                    )
                }
                is ApiKeyRepository.GetResult.Failed.NoApiKey -> {
                    FatalErrorPage(
                        errorMessage = stringResource(R.string.no_api_key_error),
                        showReportButton = false,
                        compact = compact,
                        onCopyStackTrace = onCopyStackTrace,
                        modifier = modifier,
                    )
                }
            }
        }
        is ComicVineResult.Failed.Exception -> error.run {
            FatalErrorPage(
                errorMessage = formatFetchErrorMessage(exception.toString()),
                stackTrace = exception.stackTraceToString(),
                onReport = { onReport(ErrorReportInfo(exception)) },
                compact = compact,
                onCopyStackTrace = onCopyStackTrace,
                modifier = modifier,
            )
        }
        is ComicVineResult.Failed.HttpError -> error.run {
            HttpErrorPage(
                httpCode = statusCode.code,
                onRetry = onRetry,
                compact = compact,
                modifier = modifier,
            )
        }
        ComicVineResult.Failed.NoNetworkConnection -> {
            NetworkNotAvailable(
                compact = compact,
                modifier = modifier,
            )
        }
        ComicVineResult.Failed.RequestTimeout -> {
            RetryableErrorPage(
                errorMessage = formatFetchErrorMessage(
                    stringResource(R.string.request_is_timeout),
                ),
                onRetry = onRetry,
                compact = compact,
                onCopyStackTrace = onCopyStackTrace,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun ComicVineResultErrorView(
    errorMessage: String,
    statusCode: StatusCode,
    compact: Boolean,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    FatalErrorPage(
        errorMessage = stringResource(
            R.string.service_error_template,
            errorMessage,
            statusCode.value
        ),
        showReportButton = statusCode != StatusCode.InvalidAPIKey,
        onReport = onReport,
        compact = compact,
        onCopyStackTrace = {
            clipboardManager.setText(AnnotatedString(it))
            coroutineScope.launch {
                snackbarState.showSnackbar(
                    context.getString(R.string.copied_to_clipboard),
                )
            }
        },
        modifier = modifier,
    )
}