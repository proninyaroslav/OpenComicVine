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

package org.proninyaroslav.opencomicvine.model

const val COMIC_VINE_BASE_URL = "https://comicvine.gamespot.com/"
const val COMIC_VINE_BASE_API_URL = "https://comicvine.gamespot.com/api/"

object ComicVineUrlBuilder {
    fun character(id: Int) = "${COMIC_VINE_BASE_URL}character/4005-$id/"

    fun issue(id: Int) = "${COMIC_VINE_BASE_URL}issue/4000-$id/"

    fun creator(id: Int) = "${COMIC_VINE_BASE_URL}creator/4040-$id/"

    fun publisher(id: Int) = "${COMIC_VINE_BASE_URL}publisher/4010-$id/"

    fun movie(id: Int) = "${COMIC_VINE_BASE_URL}movie/4025-$id/"

    fun storyArc(id: Int) = "${COMIC_VINE_BASE_URL}story_arc/4045-$id/"

    fun team(id: Int) = "${COMIC_VINE_BASE_URL}team/4060-$id/"

    fun person(id: Int) = "${COMIC_VINE_BASE_URL}person/4040-$id/"

    fun concept(id: Int) = "${COMIC_VINE_BASE_URL}concept/4015-$id/"

    fun location(id: Int) = "${COMIC_VINE_BASE_URL}location/4020-$id/"

    fun objectUrl(id: Int) = "${COMIC_VINE_BASE_URL}object/4055-$id/"

    fun volume(id: Int) = "${COMIC_VINE_BASE_URL}volume/4050-$id/"

    fun episode(id: Int) = "${COMIC_VINE_BASE_URL}episode/4070-$id/"

    fun series(id: Int) = "${COMIC_VINE_BASE_URL}series/4075-$id/"

    fun video(id: Int) = "${COMIC_VINE_BASE_URL}videos/video/2300-$id/"
}
