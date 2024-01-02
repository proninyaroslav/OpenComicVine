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

package org.proninyaroslav.opencomicvine.ui.image_viewer

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.ui.components.AppSnackbarHost
import org.proninyaroslav.opencomicvine.ui.components.FilledTonalActionButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageViewerPage(
    url: Uri,
    viewModel: ImageSaverViewModel,
    isExpandedWidth: Boolean,
    onBackButtonClicked: () -> Unit,
    onShare: (localImageUri: Uri, mimeType: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val imageState = rememberImageViewerState()
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentOnShare by rememberUpdatedState(onShare)

    LaunchedEffect(state) {
        val s = state
        when (s) {
            is ImageSaverState.SaveFailed.StoreError -> s.error.run {
                coroutineScope.launch {
                    val res = snackbarState.showSnackbar(
                        message = context.run {
                            getString(
                                R.string.save_image_failed,
                                exception?.run { toString() }
                                    ?: getString(R.string.save_image_failed_unknown_error)
                            )
                        },
                        actionLabel = if (exception == null) {
                            null
                        } else {
                            context.getString(R.string.report)
                        },
                        duration = SnackbarDuration.Long,
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        viewModel.errorReport(ErrorReportInfo(exception))
                    }
                }
            }

            ImageSaverState.SaveFailed.UnsupportedFormat -> coroutineScope.launch {
                snackbarState.showSnackbar(
                    context.run {
                        getString(
                            R.string.save_image_failed,
                            getString(R.string.save_image_failed_unsupported_format)
                        )
                    }
                )
            }

            is ImageSaverState.SaveSuccess -> coroutineScope.launch {
                if (s.readyToShare) {
                    currentOnShare(s.uri, s.mimeType)
                } else {
                    snackbarState.showSnackbar(context.getString(R.string.saved_to_gallery))
                }
            }

            else -> {}
        }
    }

    OpenComicVineTheme(darkTheme = true) {
        Scaffold(
            containerColor = Color.Black,
            snackbarHost = {
                AppSnackbarHost(
                    hostState = snackbarState,
                    isExpandedWidth = isExpandedWidth,
                )
            },
            modifier = modifier,
        ) {
            Box {
                ImageViewer(
                    imageState = imageState,
                    url = url,
                )
                ImageViewerAppBar(
                    onBackPressed = onBackButtonClicked,
                    actions = {
                        FilledTonalActionButton(
                            onClick = {
                                imageState.drawable?.let {
                                    viewModel.save(
                                        bitmap = (it as BitmapDrawable).bitmap,
                                        url = url,
                                        saveAndShare = true,
                                    )
                                }
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_share_24),
                                contentDescription = stringResource(R.string.share),
                            )
                        }
                        FilledTonalActionButton(
                            onClick = {
                                imageState.drawable?.let {
                                    viewModel.save(
                                        bitmap = (it as BitmapDrawable).bitmap,
                                        url = url,
                                    )
                                }
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_download_24),
                                contentDescription = stringResource(R.string.save),
                            )
                        }
                    },
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
        }
    }
}
