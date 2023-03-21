package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.AliasesConverter
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateConverter
import org.proninyaroslav.opencomicvine.model.moshi.EnumJsonAdapterFactory
import java.util.*

class IssueDetailsTest {
    @Test
    fun parse() {
        val json = mapOf(
            "date_added" to "2022-01-01 09:00:00",
            "date_last_updated" to "2022-01-01 09:00:00",
            "id" to 1,
            "image" to mapOf(
                "icon_url" to "https://example.org/icon.jpg",
                "medium_url" to "https://example.org/scale_medium/medium.jpg",
                "screen_url" to "https://example.org/screen.jpg",
                "screen_large_url" to "https://example.org/screen_large.jpg",
                "small_url" to "https://example.org/small.jpg",
                "super_url" to "https://example.org/super.jpg",
                "thumb_url" to "https://example.org/thumb.jpg",
                "tiny_url" to "https://example.org/tiny.jpg",
                "original_url" to "https://example.org/original.jpg",
                "image_tags" to "All Images",
            ),
            "name" to "test",
            "deck" to "test",
            "description" to "test",
            "issue_number" to "1",
            "volume" to mapOf("id" to "1", "name" to "test"),
            "cover_date" to "2022-01-01 09:00:00",
            "store_date" to "2022-01-01 09:00:00",
            "associated_images" to listOf(
                mapOf(
                    "id" to "1",
                    "original_url" to "https://example.org/original.jpg",
                    "caption" to "Caption",
                    "image_tags" to "All Images"
                )
            ),
            "story_arc_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "character_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "character_died_in" to listOf(mapOf("id" to "1", "name" to "test")),
            "concept_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "location_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "object_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "person_credits" to listOf(mapOf("id" to "1", "name" to "test", "role" to "role")),
            "team_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "team_disbanded_in" to listOf(mapOf("id" to "1", "name" to "test")),
        )
        val expected = IssueDetails(
            dateAdded = Date(
                GregorianCalendar(2022, 0, 1, 9, 0, 0)
                    .timeInMillis
            ),
            dateLastUpdated = Date(
                GregorianCalendar(2022, 0, 1, 9, 0, 0)
                    .timeInMillis
            ),
            descriptionShort = "test",
            description = "test",
            id = 1,
            image = ImageInfo(
                iconUrl = "https://example.org/icon.jpg",
                mediumUrl = "https://example.org/scale_medium/medium.jpg",
                screenUrl = "https://example.org/screen.jpg",
                screenLargeUrl = "https://example.org/screen_large.jpg",
                smallUrl = "https://example.org/small.jpg",
                superUrl = "https://example.org/super.jpg",
                thumbUrl = "https://example.org/thumb.jpg",
                tinyUrl = "https://example.org/tiny.jpg",
                originalUrl = "https://example.org/original.jpg",
                imageTags = "All Images",
            ),
            name = "test",
            storyArcCredits = listOf(IssueDetails.StoryArc(id = 1, name = "test")),
            issueNumber = "1",
            volume = IssueDetails.Volume(id = 1, name = "test"),
            coverDate = Date(
                GregorianCalendar(2022, 0, 1, 9, 0, 0)
                    .timeInMillis
            ),
            storeDate = Date(
                GregorianCalendar(2022, 0, 1, 9, 0, 0)
                    .timeInMillis
            ),
            associatedImages = listOf(
                IssueDetails.AssociatedImage(
                    id = 1,
                    originalUrl = "https://example.org/original.jpg",
                    caption = "Caption",
                    imageTags = "All Images",
                ),
            ),
            characterCredits = listOf(IssueDetails.Character(id = 1, name = "test")),
            characterDiedIn = listOf(IssueDetails.Character(id = 1, name = "test")),
            conceptCredits = listOf(IssueDetails.Concept(id = 1, name = "test")),
            locationCredits = listOf(IssueDetails.Location(id = 1, name = "test")),
            objectCredits = listOf(IssueDetails.Object(id = 1, name = "test")),
            personCredits = listOf(IssueDetails.Person(id = 1, name = "test", role = "role")),
            teamCredits = listOf(IssueDetails.Team(id = 1, name = "test")),
            teamDisbandedIn = listOf(IssueDetails.Team(id = 1, name = "test")),
        )
        val moshi = Moshi.Builder()
            .add(EnumJsonAdapterFactory)
            .add(ComicVineDateConverter)
            .add(AliasesConverter)
            .build()
        assertEquals(expected, moshi.parse<IssueDetails>(json))
    }
}