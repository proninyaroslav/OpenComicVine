package org.proninyaroslav.opencomicvine.model.moshi

import org.junit.Assert.*
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceType
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceTypeList

class ComicVineSearchResourceTypeListConverterTest {
    @Test
    fun toJson() {
        assertEquals(
            "character,issue",
            ComicVineSearchResourceTypeListConverter.toJson(
                ComicVineSearchResourceTypeList(
                    listOf(
                        ComicVineSearchResourceType.Character,
                        ComicVineSearchResourceType.Issue
                    ),
                )
            ),
        )
    }

    @Test
    fun fromJson() {
        assertEquals(
            ComicVineSearchResourceTypeList(
                listOf(
                    ComicVineSearchResourceType.Character,
                    ComicVineSearchResourceType.Issue
                ),
            ),
            ComicVineSearchResourceTypeListConverter.fromJson("character,issue"),
        )
    }
}