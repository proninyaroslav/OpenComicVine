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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController

infix fun NavHostController.navigateTo(destination: Destination) {
    val currentRoute = currentDestination?.route
    when (destination) {
        AppDestination.Home -> navigate(destination.route) {
            // Pop up all graph to avoid building up a large stack of destinations
            // on the back stack as users select items and close the app window on
            // back press if it's the last item on the stack
            popUpTo(0) {
                saveState = true
                HomeDestination.fromRoute(currentRoute)?.let {
                    inclusive = it != HomeDestination.Overview
                }
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }

        AppDestination.Search -> navigate(destination.route) {
            popUpTo(0) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        AppDestination.Wiki -> navigate(destination.route) {
            popUpTo(0) {
                saveState = true
                WikiDestination.fromRoute(currentRoute)?.let {
                    inclusive = it != WikiDestination.Overview
                }
            }
            launchSingleTop = true
            restoreState = true
        }

        AppDestination.Favorites -> navigate(destination.route) {
            popUpTo(0) {
                saveState = true
                FavoritesDestination.fromRoute(currentRoute)?.let {
                    inclusive = it != FavoritesDestination.Overview
                }
            }
            launchSingleTop = true
            restoreState = true
        }

        else -> navigate(destination.route) { launchSingleTop = true }
    }
}

fun <T> NavHostController.navigateTo(
    destination: Destination,
    argument: T,
) {
    navigate(destination.route(argument = argument))
}

fun NavHostController.navigateOrView(
    context: Context,
    request: NavDeepLinkRequest,
) = try {
    navigate(request)
} catch (e: IllegalArgumentException) {
    context.startActivity(Intent(Intent.ACTION_VIEW, request.uri))
}

fun NavHostController.navigateOrView(
    context: Context,
    url: Uri,
) = try {
    navigate(url)
} catch (e: IllegalArgumentException) {
    context.startActivity(Intent(Intent.ACTION_VIEW, url))
}
