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