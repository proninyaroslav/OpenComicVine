package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.Aliases

class AliasesConverterTest {
    private val converter = AliasesConverter

    @Test
    fun fromJson() {
        val json = "alias1\nalias2\nalias3"
        val expected = Aliases(listOf("alias1", "alias2", "alias3"))
        assertEquals(expected, converter.fromJson(json))
    }

    @Test
    fun toJson() {
        val aliases = Aliases(listOf("alias1", "alias2", "alias3"))
        val expected = "alias1\nalias2\nalias3"
        assertEquals(expected, converter.toJson(aliases))
    }
}