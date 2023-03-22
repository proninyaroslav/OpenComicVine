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

package org.proninyaroslav.opencomicvine.ui.about

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.ui.LocalAppSnackbarState
import org.proninyaroslav.opencomicvine.ui.components.AppLogo
import org.proninyaroslav.opencomicvine.ui.components.HyperlinkText
import org.proninyaroslav.opencomicvine.ui.components.HyperlinkTextItem
import org.proninyaroslav.opencomicvine.ui.components.error.FatalErrorPage
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import java.io.IOException

@Composable
fun AboutDialogPage(
    viewModel: AboutViewModel,
    isExpandedWidth: Boolean,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    when (val s = state) {
        is AboutState.Loaded -> {
            AboutDialog(
                appInfo = s,
                onDismissRequest = onBackButtonClicked,
                onOpenChangelog = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(context.getString(R.string.project_changelog_url)),
                        )
                    )
                },
                modifier = modifier,
            )
        }
        is AboutState.LoadFailed -> {
            ErrorDialog(
                error = s.error.exception,
                isExpandedWidth = isExpandedWidth,
                onDismissRequest = onBackButtonClicked,
                onReport = { viewModel.event(AboutEvent.ErrorReport(it)) },
            )
        }
    }
}

@Composable
private fun AboutDialog(
    appInfo: AboutState.Loaded,
    onDismissRequest: () -> Unit,
    onOpenChangelog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onOpenChangelog) {
                Text(stringResource(R.string.changelog))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.close))
            }
        },
        title = { Title(appInfo = appInfo) },
        text = { Description() },
        modifier = modifier,
    )
}

@Composable
private fun Description(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HyperlinkText(
            stringResource(R.string.app_description),
            items = listOf(
                HyperlinkTextItem(url = stringResource(R.string.project_page_url)),
                HyperlinkTextItem(url = stringResource(R.string.comic_vine_page_url)),
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        HyperlinkText(
            stringResource(R.string.app_license),
            items = listOf(
                HyperlinkTextItem(url = stringResource(R.string.gpl_3_url)),
            )
        )
    }
}

@Composable
private fun Title(
    appInfo: AboutState.Loaded,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        AppLogo(size = 64.dp)
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Text(appInfo.appName)
            Text(
                appInfo.version,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
        }
    }
}

@Composable
private fun ErrorDialog(
    error: Exception,
    isExpandedWidth: Boolean,
    onDismissRequest: () -> Unit,
    onReport: (ErrorReportInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stackTrace by remember(error) { derivedStateOf { error.stackTraceToString() } }

    val clipboardManager = LocalClipboardManager.current
    val snackbarState = LocalAppSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val containerColor = MaterialTheme.colorScheme.errorContainer
    val contentColor = MaterialTheme.colorScheme.onErrorContainer

    BoxWithConstraints {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                ErrorTextButton(onClick = { onReport(ErrorReportInfo(error)) }) {
                    Text(stringResource(R.string.report))
                }
            },
            dismissButton = {
                ErrorTextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                FatalErrorPage(
                    errorMessage = stringResource(R.string.about_app_dialog_load_failed, error),
                    stackTrace = stackTrace,
                    compact = true,
                    showReportButton = false,
                    onCopyStackTrace = {
                        clipboardManager.setText(AnnotatedString(stackTrace))
                        coroutineScope.launch {
                            snackbarState.showSnackbar(
                                context.getString(R.string.copied_to_clipboard),
                            )
                        }
                    },
                )
            },
            containerColor = containerColor,
            iconContentColor = contentColor,
            titleContentColor = contentColor,
            textContentColor = contentColor,
            properties = DialogProperties(
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
            modifier = modifier
                .padding(16.dp)
                .width(if (isExpandedWidth) maxWidth / 2 else maxWidth),
        )
    }
}

@Composable
private fun ErrorTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
        ),
        onClick = onClick,
        content = content,
        modifier = modifier,
    )
}

@Preview
@Composable
fun PreviewAboutDialog() {
    OpenComicVineTheme {
        AboutDialog(
            appInfo = AboutState.Loaded(
                appName = "OpenComicVine",
                version = "1.0",
            ),
            onOpenChangelog = {},
            onDismissRequest = {},
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAboutDialog_Dark() {
    OpenComicVineTheme {
        AboutDialog(
            appInfo = AboutState.Loaded(
                appName = "OpenComicVine",
                version = "1.0",
            ),
            onOpenChangelog = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
fun PreviewErrorDialog() {
    OpenComicVineTheme {
        val snackbarState = remember { SnackbarHostState() }
        CompositionLocalProvider(LocalAppSnackbarState provides snackbarState) {
            ErrorDialog(
                error = IOException(),
                isExpandedWidth = false,
                onDismissRequest = {},
                onReport = {},
            )
        }
    }
}
