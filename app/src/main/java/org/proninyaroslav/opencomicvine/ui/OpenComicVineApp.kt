package org.proninyaroslav.opencomicvine.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.proninyaroslav.opencomicvine.data.preferences.PrefTheme
import org.proninyaroslav.opencomicvine.ui.components.AppSnackbarHost
import org.proninyaroslav.opencomicvine.ui.navigation.AppNavBarScaffold
import org.proninyaroslav.opencomicvine.ui.navigation.AppNavGraph
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import org.proninyaroslav.opencomicvine.ui.theme.ThemeViewModel

@Composable
fun OpenComicVineApp(
    themeViewModel: ThemeViewModel,
    widthSizeClass: WindowWidthSizeClass,
) {
    val themeState by themeViewModel.theme.collectAsStateWithLifecycle()

    OpenComicVineTheme(
        darkTheme = when (themeState) {
            PrefTheme.Dark -> true
            PrefTheme.Light -> false
            PrefTheme.System,
            PrefTheme.Unknown -> isSystemInDarkTheme()
        },
    ) {
        val navController = rememberNavController()
        val isExpandedWidth = widthSizeClass == WindowWidthSizeClass.Expanded

        AppNavBarScaffold(
            navController = navController,
            snackbarHost = {
                AppSnackbarHost(
                    hostState = LocalAppSnackbarState.current,
                    isExpandedWidth = isExpandedWidth,
                )
            },
            isExpandedWidth = isExpandedWidth,
        ) { contentPadding ->
            AppNavGraph(
                navController = navController,
                isExpandedWidth = isExpandedWidth,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}