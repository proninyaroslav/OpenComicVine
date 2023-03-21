package org.proninyaroslav.opencomicvine.ui.components.drawer

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun CustomPermanentNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit,
    direction: LayoutDirection = LayoutDirection.Rtl,
    content: @Composable () -> Unit
) {
    val originalLayoutDirection = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        PermanentNavigationDrawer(
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection) {
                    drawerContent()
                }
            },
            modifier = modifier.statusBarsPadding(),
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides originalLayoutDirection) {
                    content()
                }
            },
        )
    }
}