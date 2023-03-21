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
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
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
    val imageState = rememberImageViewerState()
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentOnShare by rememberUpdatedState(onShare)

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ImageSaverEffect.SaveFailed.StoreError -> effect.error.run {
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
                            viewModel.event(
                                ImageSaverEvent.ErrorReport(
                                    ErrorReportInfo(exception)
                                )
                            )
                        }
                    }
                }
                ImageSaverEffect.SaveFailed.UnsupportedFormat -> coroutineScope.launch {
                    snackbarState.showSnackbar(
                        context.run {
                            getString(
                                R.string.save_image_failed,
                                getString(R.string.save_image_failed_unsupported_format)
                            )
                        }
                    )
                }
                is ImageSaverEffect.SaveSuccess -> coroutineScope.launch {
                    snackbarState.showSnackbar(context.getString(R.string.saved_to_gallery))
                }
                is ImageSaverEffect.ReadyToShare -> effect.run {
                    currentOnShare(uri, mimeType)
                }
            }
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
                                    viewModel.event(
                                        ImageSaverEvent.SaveForSharing(
                                            bitmap = (it as BitmapDrawable).bitmap,
                                            url = url,
                                        )
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
                                    viewModel.event(
                                        ImageSaverEvent.Save(
                                            bitmap = (it as BitmapDrawable).bitmap,
                                            url = url,
                                        )
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