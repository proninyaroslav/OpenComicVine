package org.proninyaroslav.opencomicvine.ui.auth

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
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage

@Composable
fun AuthErrorView(
    error: ApiKeyRepository.SaveResult.Failed,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    when (error) {
        is ApiKeyRepository.SaveResult.Failed.IO -> FatalErrorPage(
            errorMessage = stringResource(
                R.string.save_api_key_error_template,
                error.exception,
            ),
            stackTrace = error.exception.stackTraceToString(),
            compact = true,
            onReport = { onReport(ErrorReportInfo(error.exception)) },
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
}