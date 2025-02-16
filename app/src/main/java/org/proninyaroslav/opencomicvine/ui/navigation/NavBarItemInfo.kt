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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.proninyaroslav.opencomicvine.R

enum class NavBarItemInfo(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val destination: AppDestination,
) {
    Home(
        icon = R.drawable.ic_home_24,
        label = R.string.nav_bar_home,
        destination = AppDestination.Home,
    ),

    Search(
        icon = R.drawable.ic_search_24,
        label = R.string.nav_bar_search,
        destination = AppDestination.Search,
    ),

    Wiki(
        icon = R.drawable.ic_menu_book_24,
        label = R.string.nav_bar_wiki,
        destination = AppDestination.Wiki,
    ),

    Favorites(
        icon = R.drawable.ic_favorite_24,
        label = R.string.nav_bar_favorites,
        destination = AppDestination.Favorites
    );

    companion object {
        private val pathToAppDestinationMap = entries.associateBy { it.destination.route }

        fun fromPath(path: String?): NavBarItemInfo? = pathToAppDestinationMap[path]
    }
}
