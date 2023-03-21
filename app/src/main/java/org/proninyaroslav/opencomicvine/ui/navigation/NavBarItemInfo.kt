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
        private val pathToAppDestinationMap = values().associateBy { it.destination.route }

        fun fromPath(path: String?): NavBarItemInfo? = pathToAppDestinationMap[path]
    }
}