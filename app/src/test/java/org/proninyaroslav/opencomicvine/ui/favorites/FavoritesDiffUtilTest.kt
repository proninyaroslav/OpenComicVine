package org.proninyaroslav.opencomicvine.ui.favorites

import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.FavoriteInfo
import java.util.*

class FavoritesDiffUtilTest {
    @Test
    fun compare() {
        val diffUtil = FavoritesDiffUtil()
        val infoList = List(3) {
            FavoriteInfo(
                id = it,
                entityId = it,
                entityType = FavoriteInfo.EntityType.Character,
                dateAdded = Calendar.getInstance().time,
            )
        }
        assertEquals(
            FavoritesDiffUtil.Result(
                addedItems = infoList.subList(0, 2),
                removedItems = emptyList(),
            ),
            diffUtil.compare(infoList.subList(0, 2))
        )

        assertEquals(
            FavoritesDiffUtil.Result(
                addedItems = listOf(infoList[2]),
                removedItems = emptyList(),
            ),
            diffUtil.compare(infoList.subList(0, 3))
        )

        assertEquals(
            FavoritesDiffUtil.Result(
                addedItems = emptyList(),
                removedItems = listOf(infoList[2]),
            ),
            diffUtil.compare(infoList.subList(0, 2))
        )
    }
}