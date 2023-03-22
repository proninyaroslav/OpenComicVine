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

package org.proninyaroslav.opencomicvine.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavBarScaffold(
    navController: NavHostController,
    snackbarHost: @Composable () -> Unit,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedItem by remember(navBackStackEntry) {
        derivedStateOf {
            val route = navBackStackEntry?.destination?.hierarchy
                ?.find { NavBarItemInfo.fromPath(it.route) != null }
                ?.route
            NavBarItemInfo.fromPath(route)
        }
    }
    Scaffold(
        bottomBar = {
            if (!isExpandedWidth) {
                AppNavBar(
                    selectedItem = selectedItem,
                    onSelected = { item ->
                        item.navigate(navController)
                    },
                )
            }
        },
        snackbarHost = snackbarHost,
        modifier = modifier,
    ) { contentPadding ->
        val direction = LocalLayoutDirection.current
        val contentPaddingWithoutTop = PaddingValues(
            start = contentPadding.calculateStartPadding(direction),
            end = contentPadding.calculateEndPadding(direction),
            bottom = contentPadding.calculateBottomPadding(),
        )
        Row(
            modifier = modifier.fillMaxSize()
        ) {
            if (isExpandedWidth) {
                AppNavRail(
                    selectedItem = selectedItem,
                    onSelected = { item ->
                        item.navigate(navController)
                    },
                )
            }
            content(contentPaddingWithoutTop)
        }
    }
}

private fun NavBarItemInfo.navigate(navController: NavHostController) {
    when (this) {
        NavBarItemInfo.Home -> navController navigateTo AppDestination.Home
        NavBarItemInfo.Search -> navController navigateTo AppDestination.Search
        NavBarItemInfo.Wiki -> navController navigateTo AppDestination.Wiki
        NavBarItemInfo.Favorites -> navController navigateTo AppDestination.Favorites
    }
}
