package org.proninyaroslav.opencomicvine.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope

val LocalActivity = staticCompositionLocalOf<AppCompatActivity> {
    noLocalProvidedFor("LocalActivity")
}

val LocalAppSnackbarState = staticCompositionLocalOf<SnackbarHostState> {
    noLocalProvidedFor("LocalAppSnackbarState")
}

val LocalAppCoroutineScope = staticCompositionLocalOf<CoroutineScope> {
    noLocalProvidedFor("LocalAppCoroutineScope")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

@Composable
fun AppCompositionLocalProvider(
    activity: AppCompatActivity,
    snackbarState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalActivity provides activity,
        LocalAppSnackbarState provides snackbarState,
        LocalAppCoroutineScope provides coroutineScope,
        content = content,
    )
}