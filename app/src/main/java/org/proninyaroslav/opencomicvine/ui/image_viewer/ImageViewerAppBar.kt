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

import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.ui.components.FilledTonalBackButton
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImageViewerAppBar(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        coroutineScope.launch {
            delay(InitialVisibilityDuration)
            visible = false
        }
    }

    AnimatedHideAppBar(
        visible = visible,
    ) { show ->
        if (show) {
            TopAppBar(
                title = {},
                navigationIcon = {
                    FilledTonalBackButton(onClick = onBackPressed)
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                modifier = modifier,
            )
        } else {
            SwipeArea(
                onVisibilityChange = {
                    if (visible != it) {
                        visible = it
                        coroutineScope.launch {
                            delay(VisibilityDuration)
                            visible = false
                        }
                    }
                },
            )
        }
    }
}

private const val InitialVisibilityDuration = 1000L
private const val VisibilityDuration = 2500L

@Composable
private fun SwipeArea(
    onVisibilityChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .height(64.0.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val y = dragAmount.y
                    // Swipe down
                    if (y > 0) {
                        onVisibilityChange(true)
                    }
                }
            },
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedHideAppBar(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(targetState: Boolean) -> Unit,
) {
    AnimatedContent(
        targetState = visible,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInVertically { fullHeight ->
                    -fullHeight
                } + fadeIn(
                    initialAlpha = 0.3f
                ),
                initialContentExit = slideOutVertically { fullHeight ->
                    -fullHeight
                } + fadeOut(),
            )
        },
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewImageViewerAppBar() {
    OpenComicVineTheme {
        ImageViewerAppBar(
            onBackPressed = {},
        )
    }
}
