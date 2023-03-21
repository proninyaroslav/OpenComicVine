package org.proninyaroslav.opencomicvine.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.model.repo.ApiKeyRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@Composable
fun AuthLoadingError(
    error: ApiKeyRepository.GetResult.Failed.IO,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        FatalErrorPage(
            errorMessage = stringResource(R.string.error_get_auth_status_template, error.exception),
            stackTrace = error.exception.stackTraceToString(),
            showReportButton = false,
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

@Preview
@Composable
fun PreviewAuthLoadingError() {
    OpenComicVineTheme {
        AuthLoadingError(
            error = ApiKeyRepository.GetResult.Failed.IO(IOException())
        )
    }
}