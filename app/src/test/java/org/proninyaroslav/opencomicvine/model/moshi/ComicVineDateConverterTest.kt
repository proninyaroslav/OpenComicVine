package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ComicVineDateConverterTest {
    private val converter = ComicVineDateConverter

    @Test
    fun fromJson() {
        assertEquals(
            Date(
                GregorianCalendar(2008, 5, 6, 11, 27, 37)
                    .timeInMillis
            ),
            converter.fromJson("2008-06-06 11:27:37")
        )
        assertEquals(
            Date(
                GregorianCalendar(2008, 5, 6)
                    .timeInMillis
            ),
            converter.fromJson("2008-06-06")
        )
        assertEquals(
            Date(
                GregorianCalendar(2008, 5, 6)
                    .timeInMillis
            ),
            converter.fromJson("Jun 6, 2008")
        )
    }

    @Test
    fun toJson() {
        val date = Date(
            GregorianCalendar(2008, 5, 6, 11, 27, 37)
                .timeInMillis
        )
        assertEquals("2008-06-06 11:27:37", converter.toJson(date))
    }
}