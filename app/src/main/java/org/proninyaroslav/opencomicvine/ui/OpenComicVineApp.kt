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

package org.proninyaroslav.opencomicvine.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.proninyaroslav.opencomicvine.types.preferences.PrefTheme
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
