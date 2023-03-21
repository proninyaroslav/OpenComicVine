package org.proninyaroslav.opencomicvine.ui.favorites

import org.proninyaroslav.opencomicvine.data.FavoriteInfo

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