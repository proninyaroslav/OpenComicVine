package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.EnumJsonAdapterFactory

class GenderTest {
    @Test
    fun parse() {
        val moshi = Moshi.Builder().add(EnumJsonAdapterFactory).build()
        assertEquals(Gender.Male.name, Gender.Male, moshi.parse<Gender>("1"))
        assertEquals(Gender.Female.name, Gender.Female, moshi.parse<Gender>("2"))
        assertEquals(Gender.Other.name, Gender.Other, moshi.parse<Gender>("0"))
    }
}