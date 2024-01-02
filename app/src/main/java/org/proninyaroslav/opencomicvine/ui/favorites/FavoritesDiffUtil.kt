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

package org.proninyaroslav.opencomicvine.ui.favorites

import org.proninyaroslav.opencomicvine.types.FavoriteInfo

class FavoritesDiffUtil {
    private var currentSet = mutableSetOf<FavoriteInfo>()

    fun compare(newItems: List<FavoriteInfo>): Result {
        if (currentSet.isEmpty()) {
            currentSet += newItems
            return Result(
                addedItems = newItems,
                removedItems = emptyList(),
            )
        }

        val newSet = mutableSetOf<FavoriteInfo>()
        val addedItems = mutableListOf<FavoriteInfo>()
        val removedItems = mutableListOf<FavoriteInfo>()
        newItems.onEach { id ->
            newSet += id
            if (!currentSet.contains(id)) {
                addedItems += id
            }
        }
        currentSet.onEach { id ->
            if (!newItems.contains(id)) {
                removedItems += id
            }
        }

        currentSet = newSet

        return Result(
            addedItems = addedItems,
            removedItems = removedItems,
        )
    }

    data class Result(
        val addedItems: List<FavoriteInfo>,
        val removedItems: List<FavoriteInfo>,
    )
}
