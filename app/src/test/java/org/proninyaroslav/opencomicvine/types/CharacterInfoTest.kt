package org.proninyaroslav.opencomicvine.types

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateConverter
import java.util.*

class CharacterInfoTest {
    @Test
    fun parse() {
        val json = mapOf(
            "id" to 1,
            "name" to "Foo Bar",
            "gender" to Gender.Male,
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
            "date_added" to "2022-01-01 09:01:01",
            "date_last_updated" to "2022-02-01 09:01:01"
        )

        val expectedCharacter = CharacterInfo(
            id = 1,
            name = "Foo Bar",
            gender = Gender.Male,
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
            dateAdded = Date(
                GregorianCalendar(2022, 0, 1, 9, 1, 1)
                    .timeInMillis
            ),
            dateLastUpdated = Date(
                GregorianCalendar(2022, 1, 1, 9, 1, 1)
                    .timeInMillis
            ),
        )

        val moshi = Moshi.Builder()
            .add(ComicVineDateConverter)
            .build()
        assertEquals(expectedCharacter, moshi.parse<CharacterInfo>(json))
    }
}