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

interface Destination {
    val value: String
    val argumentName: String?
    val parent: Destination?
    val deepLinks: List<DeepLink>
        get() = emptyList()

    data class DeepLink(
        val uriPattern: String? = null,
        val intentAction: String? = null,
        val mimeType: String? = null,
    ) {
        init {
            check(!(uriPattern == null && intentAction == null && mimeType == null)) {
                ("The DeepLink must have an uri, action, and/or mimeType.")
            }
        }
    }
}

private val Destination.baseRoute: String
    get() = "${parent?.route ?: ""}$value"

val Destination.route: String
    get() = "$baseRoute/${argumentName?.let { "{$argumentName}/" } ?: ""}"

fun <T> Destination.route(argument: T): String =
    "$baseRoute/${this.argumentName?.let { "$argument/" } ?: ""}"

enum class AppDestination(
    override val value: String,
    override val argumentName: String? = null,
) : Destination {
    Home("home"),
    Search("search"),
    Wiki("wiki"),
    Favorites("favorites"),
    ImageViewer(
        "image_viewer",
        argumentName = "url",
    ),
    Auth("auth");

    override val parent: Destination? = null
}

enum class HomeDestination(
    override val value: String,
    override val argumentName: String? = null,
    override val deepLinks: List<Destination.DeepLink> = emptyList(),
) : Destination {
    Overview("overview"),
    Settings("settings"),
    About("about"),
    RecentCharacters("recent_characters"),
    RecentIssues(
        "recent_issues",
        deepLinks = NavDeepLinkUrl.recentIssues.map {
            Destination.DeepLink(uriPattern = it)
        },
    ),
    RecentVolumes("recent_volumes"),
    Character(
        "character",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.character("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Home.value,
            )
        },
    ),
    Issue(
        "issue",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.issue("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Home.value,
            )
        },
    ),
    Volume(
        "volume",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.volume("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Home.value,
            )
        },
    );

    override val parent: Destination? = AppDestination.Home

    companion object {
        private val routeToDestinationMap = values().associateBy { it.route }
        fun fromRoute(route: String?): HomeDestination? = routeToDestinationMap[route]
    }
}

enum class WikiDestination(
    override val value: String,
    override val argumentName: String? = null,
    override val deepLinks: List<Destination.DeepLink> = emptyList(),
) : Destination {
    Overview("overview"),
    Characters(
        "characters",
        deepLinks = NavDeepLinkUrl.characters.map { Destination.DeepLink(uriPattern = it) }
    ),
    Issues(
        "issues",
        deepLinks = NavDeepLinkUrl.issues.map { Destination.DeepLink(uriPattern = it) },
    ),
    Volumes(
        "volumes",
        deepLinks = NavDeepLinkUrl.volumes.map { Destination.DeepLink(uriPattern = it) },
    ),
    Character(
        "character",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.character("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Wiki.value,
            )
        },
    ),
    Issue(
        "issue",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.issue("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Wiki.value,
            )
        },
    ),
    Volume(
        "volume",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.volume("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Wiki.value,
            )
        },
    );

    override val parent: Destination? = AppDestination.Wiki

    companion object {
        private val routeToDestinationMap = values().associateBy { it.route }
        fun fromRoute(route: String?): WikiDestination? = routeToDestinationMap[route]
    }
}

enum class FavoritesDestination(
    override val value: String,
    override val argumentName: String? = null,
    override val deepLinks: List<Destination.DeepLink> = emptyList(),
) : Destination {
    Overview("overview"),
    Characters("characters"),
    Issues("issues"),
    Volumes("volumes"),
    Concepts("concepts"),
    Locations("locations"),
    Movies("movies"),
    Objects("objects"),
    People("people"),
    StoryArcs("story_arcs"),
    Teams("teams"),
    Character(
        "character",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.character("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Favorites.value,
            )
        },
    ),
    Issue(
        "issue",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.issue("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Favorites.value,
            )
        },
    ),
    Volume(
        "volume",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.volume("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Favorites.value,
            )
        },
    );

    override val parent: Destination? = AppDestination.Favorites

    companion object {
        private val routeToDestinationMap = values().associateBy { it.route }
        fun fromRoute(route: String?): FavoritesDestination? = routeToDestinationMap[route]
    }
}

enum class SearchDestination(
    override val value: String,
    override val argumentName: String? = null,
    override val deepLinks: List<Destination.DeepLink> = emptyList(),
) : Destination {
    Overview("overview"),
    Character(
        "character",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.character("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Search.value,
            )
        },
    ),
    Issue(
        "issue",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.issue("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Search.value,
            )
        },
    ),
    Volume(
        "volume",
        argumentName = "id",
        deepLinks = NavDeepLinkUrl.volume("id").map {
            Destination.DeepLink(
                uriPattern = it,
                intentAction = AppDestination.Search.value,
            )
        },
    );

    override val parent: Destination? = AppDestination.Search

    companion object {
        private val routeToDestinationMap = values().associateBy { it.route }
    }
}
