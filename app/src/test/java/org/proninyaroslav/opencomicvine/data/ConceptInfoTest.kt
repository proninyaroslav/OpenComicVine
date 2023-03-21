package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateConverter

class ConceptInfoTest {
    @Test
    fun parse() {
        val json = mapOf(
            "id" to 1,
            "name" to "Foo Bar",
            "image" to mapOf(
                "icon_url" to "https://example.org/icon.jpg",
                "medium_url" to "https://example.org/medium.jpg",
                "screen_url" to "https://example.org/screen.jpg",
                "screen_large_url" to "https://example.org/screen_large.jpg",
                "small_url" to "https://example.org/small.jpg",
                "super_url" to "https://example.org/super.jpg",
                "thumb_url" to "https://example.org/thumb.jpg",
                "tiny_url" to "https://example.org/tiny.jpg",
                "original_url" to "https://example.org/original.jpg",
                "image_tags" to "All Images",
            ),
        )

        val expectedCharacter = ConceptInfo(
            id = 1,
            name = "Foo Bar",
            image = ImageInfo(
                iconUrl = "https://example.org/icon.jpg",
                mediumUrl = "https://example.org/medium.jpg",
                screenUrl = "https://example.org/screen.jpg",
                screenLargeUrl = "https://example.org/screen_large.jpg",
                smallUrl = "https://example.org/small.jpg",
                superUrl = "https://example.org/super.jpg",
                thumbUrl = "https://example.org/thumb.jpg",
                tinyUrl = "https://example.org/tiny.jpg",
                originalUrl = "https://example.org/original.jpg",
                imageTags = "All Images",
            ),
        )

        val moshi = Moshi.Builder()
            .add(ComicVineDateConverter)
            .build()
        assertEquals(expectedCharacter, moshi.parse<ConceptInfo>(json))
    }
}