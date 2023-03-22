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

class NavDeepLinkUrl {
    companion object {
        val characters = listOf("https://comicvine.gamespot.com/characters/")

        val issues = listOf("https://comicvine.gamespot.com/issues/")

        val volumes = listOf("https://comicvine.gamespot.com/volumes/")

        val recentIssues = listOf("https://comicvine.gamespot.com/new-comics/")

        fun character(idArgName: String) = listOf(
            "https://comicvine.gamespot.com/{_nameIdStub}/4005-{$idArgName}/",
            "https://comicvine.gamespot.com/{_nameIdStub}/29-{$idArgName}/",
        )

        fun issue(idArgName: String) =
            listOf("https://comicvine.gamespot.com/{_nameIdStub}/4000-{$idArgName}/")

        fun volume(idArgName: String) =
            listOf("https://comicvine.gamespot.com/{_nameIdStub}/4050-{$idArgName}/")
    }
}
