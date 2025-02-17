/*
 * Copyright (C) 2023-2024 Yaroslav Pronin <proninyaroslav@mail.ru>
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

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.ui.AppCompositionLocalProvider
import org.proninyaroslav.opencomicvine.ui.navigation.AppDestination

@AndroidEntryPoint
class ImageViewerActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val snackbarState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            AppCompositionLocalProvider(
                activity = this,
                snackbarState = snackbarState,
                coroutineScope = coroutineScope,
            ) {
                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                val base64 = intent.getStringExtra(AppDestination.ImageViewer.argumentName)!!
                val url = Base64.decode(base64, Base64.DEFAULT).decodeToString().toUri()
                ImageViewerPage(
                    url = url,
                    viewModel = viewModel(),
                    isExpandedWidth = widthSizeClass == WindowWidthSizeClass.Expanded,
                    onShare = { uri, mimeType ->
                        shareImage(
                            localImageUri = uri,
                            mimeType = mimeType,
                        )
                    },
                    onBackButtonClicked = ::finish,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        enableFullscreenMode()
    }

    private fun enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.run {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun shareImage(
        localImageUri: Uri,
        mimeType: String,
    ) {
        ShareCompat.IntentBuilder(this)
            .setType(mimeType)
            .setChooserTitle(getString(R.string.share))
            .setStream(localImageUri)
            .startChooser()
    }
}
