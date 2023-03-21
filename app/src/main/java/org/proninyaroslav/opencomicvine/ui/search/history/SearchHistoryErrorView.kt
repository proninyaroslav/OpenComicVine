package org.proninyaroslav.opencomicvine.ui.search.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage

@Composable
fun SearchHistoryErrorView(
    error: SearchHistoryRepository.Result.Failed,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    when (error) {
        is SearchHistoryRepository.Result.Failed.IO -> {
            FatalErrorPage(
                errorMessage = stringResource(
                    R.string.fetch_search_history_list_error_template,
                    error.exception.toString()
                ),
                stackTrace = error.exception.stackTraceToString(),
                showReportButton = false,
                compact = true,
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
}