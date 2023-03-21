package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) }
) {
    val mod = if (isExpandedWidth) {
        modifier
            .systemBarsPadding()
            .imePadding()
    } else {
        modifier
    }
    SnackbarHost(
        hostState = hostState,
        modifier = mod
            // Limit the Snackbar width for large screens
            .wrapContentWidth(align = Alignment.Start)
            .widthIn(max = 550.dp),
        snackbar = snackbar
    )
}