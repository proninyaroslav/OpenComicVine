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

package org.proninyaroslav.opencomicvine.ui.details

sealed interface DetailsPage {
    data class Character(val characterId: Int) : DetailsPage
    data class Issue(val issueId: Int) : DetailsPage
    data class Volume(val volumeId: Int) : DetailsPage
    data class Publisher(val publisherId: Int) : DetailsPage
    data class Creator(val creatorId: Int) : DetailsPage
    data class Movie(val movieId: Int) : DetailsPage
    data class StoryArc(val storyArcId: Int) : DetailsPage
    data class Team(val teamId: Int) : DetailsPage
    data class Person(val personId: Int) : DetailsPage
    data class Location(val locationId: Int) : DetailsPage
    data class Concept(val conceptId: Int) : DetailsPage
    data class Object(val objectId: Int) : DetailsPage
    data class ImageViewer(val url: String) : DetailsPage
}
