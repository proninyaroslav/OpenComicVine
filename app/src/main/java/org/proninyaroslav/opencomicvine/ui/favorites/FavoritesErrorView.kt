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

package org.proninyaroslav.opencomicvine.ui.favorites

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
import org.proninyaroslav.opencomicvine.model.paging.favorites.FavoritesEntityRemoteMediator
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.error.ComicVineResultErrorView
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage
import org.proninyaroslav.opencomicvine.ui.components.error.RetryableErrorPage

@Composable
fun FavoritesErrorView(
    state: LoadState.Error,
    toMediatorError: (LoadState.Error) -> FavoritesEntityRemoteMediator.Error?,
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
            is FavoritesEntityRemoteMediator.Error.Service -> {
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
            is FavoritesEntityRemoteMediator.Error.Fetching -> {
                ComicVineResultErrorView(
                    error = error.error,
                    formatFetchErrorMessage = formatFetchErrorMessage,
                    onRetry = onRetry,
                    onReport = onReport,
                    compact = compact,
                    modifier = modifier,
                )
            }
            is FavoritesEntityRemoteMediator.Error.Save.IO -> RetryableErrorPage(
                errorMessage = formatSaveErrorMessage(error.exception.toString()),
                stackTrace = error.exception.stackTraceToString(),
                onRetry = onRetry,
                compact = compact,
                onCopyStackTrace = onCopyStackTrace,
                modifier = modifier,
            )
            is FavoritesEntityRemoteMediator.Error.IO -> RetryableErrorPage(
                errorMessage = formatFetchErrorMessage(error.exception.toString()),
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
