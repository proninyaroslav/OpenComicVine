package org.proninyaroslav.opencomicvine.ui.components.drawer

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun CustomModalNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit,
    direction: LayoutDirection = LayoutDirection.Rtl,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    val originalLayoutDirection = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        ModalNavigationDrawer(
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection) {
                    drawerContent()
                }
            },
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            scrimColor = scrimColor,
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection) {
                    content()
                }
            },
        )
    }
}