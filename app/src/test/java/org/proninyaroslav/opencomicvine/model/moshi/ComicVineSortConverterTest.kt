package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSort
import org.proninyaroslav.opencomicvine.types.sort.ComicVineSortDirection

class ComicVineSortConverterTest {
    private val converter = ComicVineSortConverter

    @Test
    fun toJson() {
        val sort = ComicVineSort(
            field = "myField",
            direction = ComicVineSortDirection.Asc,
        )
        assertEquals("myField:asc", converter.toJson(sort))
    }

    @Test
    fun fromJson() {
        val sort = "myField:asc"
        assertEquals(
            ComicVineSort(
                field = "myField",
                direction = ComicVineSortDirection.Asc,
            ),
            converter.fromJson(sort)
        )
    }
}