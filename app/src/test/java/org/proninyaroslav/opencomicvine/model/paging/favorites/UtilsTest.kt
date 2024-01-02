package org.proninyaroslav.opencomicvine.model.paging.favorites

import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.item.favorites.FavoritesCharacterItem
import org.proninyaroslav.opencomicvine.types.preferences.PrefFavoritesSort
import org.proninyaroslav.opencomicvine.types.preferences.PrefSortDirection
import java.util.*

class UtilsTest {
    @Test
    fun sort() {
        val list = listOf(
            FavoritesCharacterItem(
                info = mockk(),
                dateAdded = Date(
                    GregorianCalendar(2023, 0, 1).timeInMillis
                ),
            ),
            FavoritesCharacterItem(
                info = mockk(),
                dateAdded = Date(
                    GregorianCalendar(2023, 0, 3).timeInMillis
                ),
            ),
            FavoritesCharacterItem(
                info = mockk(),
                dateAdded = Date(
                    GregorianCalendar(2023, 0, 2).timeInMillis
                ),
            ),
        )

        assertEquals(
            list.sortedBy { it.dateAdded },
            list.sort(PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Asc)),
        )

        assertEquals(
            list.sortedByDescending { it.dateAdded },
            list.sort(PrefFavoritesSort.DateAdded(direction = PrefSortDirection.Desc)),
        )
    }
}