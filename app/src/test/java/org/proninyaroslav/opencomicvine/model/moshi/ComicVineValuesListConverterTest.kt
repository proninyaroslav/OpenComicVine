package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.assertEquals
import org.junit.Test

class ComicVineValuesListConverterTest {
    private val converter = ComicVineValuesListConverter { it.toInt() }

    @Test
    fun fromJson() {
        val list = "1|2|3"
        assertEquals(listOf(1, 2, 3), converter.fromJson(list))
    }

    @Test
    fun toJson() {
        val list = listOf(1, 2, 3)
        assertEquals("1|2|3", converter.toJson(list))
    }
}