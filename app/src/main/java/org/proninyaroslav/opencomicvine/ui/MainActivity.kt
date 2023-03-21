package org.proninyaroslav.opencomicvine.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val snackbarState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            AppCompositionLocalProvider(
                activity = this,
                snackbarState = snackbarState,
                coroutineScope = coroutineScope,
            ) {
                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                OpenComicVineApp(
                    themeViewModel = hiltViewModel(),
                    widthSizeClass = widthSizeClass,
                )
            }
        }
    }
}