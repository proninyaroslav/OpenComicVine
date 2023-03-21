package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ComicVineDateRangeConverterTest {
    private val converter = ComicVineDateRangeConverter

    @Test
    fun fromJson() {
        val range = "2022-01-01|2022-01-02"
        assertEquals(
            Date(
                GregorianCalendar(2022, 0, 1).timeInMillis,
            ) to Date(
                GregorianCalendar(2022, 0, 2).timeInMillis,
            ),
            converter.fromJson(range),
        )
    }

    @Test
    fun toJson() {
        val range = Date(
            GregorianCalendar(2022, 0, 1).timeInMillis,
        ) to Date(
            GregorianCalendar(2022, 0, 2).timeInMillis,
        )
        assertEquals("2022-01-01|2022-01-02", converter.toJson(range))
    }
}