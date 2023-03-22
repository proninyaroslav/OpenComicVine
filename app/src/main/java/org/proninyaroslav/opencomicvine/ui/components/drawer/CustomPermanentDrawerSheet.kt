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

package org.proninyaroslav.opencomicvine.ui.components.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import org.proninyaroslav.opencomicvine.ui.inverse

private val DefaultFloatingActionButtonComposable: @Composable BoxScope.() -> Unit = {}

@Composable
fun CustomPermanentDrawerSheet(
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable BoxScope.() -> Unit = DefaultFloatingActionButtonComposable,
    lazyListState: LazyListState = rememberLazyListState(),
    drawerShape: Shape = DrawerDefaults.shape,
    drawerTonalElevation: Dp = DrawerDefaults.PermanentDrawerElevation,
    drawerContainerColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: LazyListScope.() -> Unit
) {
    val originalLayoutDirection = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection.inverse()) {
        PermanentDrawerSheet(
            drawerShape = drawerShape,
            drawerTonalElevation = drawerTonalElevation,
            drawerContainerColor = drawerContainerColor,
            drawerContentColor = drawerContentColor,
            windowInsets = windowInsets,
            modifier = modifier,
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        content = content,
                    )
                    floatingActionButton()
                }
            }
        }
    }
}
