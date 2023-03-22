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
