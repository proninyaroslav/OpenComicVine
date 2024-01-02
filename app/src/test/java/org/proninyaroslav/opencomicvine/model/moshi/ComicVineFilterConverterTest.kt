package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.types.filter.ComicVineFilter

class ComicVineFilterConverterTest {
    private val converter = ComicVineFilterConverter

    @Test
    fun toJson() {
        val filter = ComicVineFilter(
            field = "myField",
            value = "myValue",
        )
        assertEquals("myField:myValue", converter.toJson(filter))
    }

    @Test
    fun fromJson() {
        val filter = "myField:myValue"
        assertEquals(
            ComicVineFilter(
                field = "myField",
                value = "myValue",
            ),
            converter.fromJson(filter)
        )
    }
}