package org.proninyaroslav.opencomicvine.types

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test

class OriginTest {
    @Test
    fun parse() {
        val json = mapOf(
            "id" to 1,
            "name" to "test",
        )
        val expected = Origin(
            id = 1,
            name = "test",
        )
        val moshi = Moshi.Builder().build()
        assertEquals(expected, moshi.parse<Origin>(json))
    }
}