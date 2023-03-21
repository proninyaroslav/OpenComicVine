package org.proninyaroslav.opencomicvine.data

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.AliasesConverter
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateConverter
import org.proninyaroslav.opencomicvine.model.moshi.EnumJsonAdapterFactory
import java.util.*

class CharacterDetailsTest {
    @Test
    fun parse() {
        val json = mapOf(
            "aliases" to "alias1\nalias2\nalias3",
            "birth" to "Jan 1, 2022",
            "character_enemies" to listOf(mapOf("id" to "1", "name" to "test")),
            "character_friends" to listOf(mapOf("id" to "1", "name" to "test")),
            "count_of_issue_appearances" to 1,
            "creators" to listOf(mapOf("id" to "1", "name" to "test")),
            "date_added" to "2022-01-01 09:00:00",
            "date_last_updated" to "2022-01-01 09:00:00",
            "deck" to "test",
            "description" to "test",
            "first_appeared_in_issue" to mapOf(
                "id" to "1",
                "name" to "test",
                "issue_number" to "1",
            ),
            "gender" to 1,
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
            "issue_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "issues_died_in" to listOf(mapOf("id" to "1", "name" to "test")),
            "movies" to listOf(mapOf("id" to "1", "name" to "test")),
            "name" to "test",
            "origin" to mapOf("id" to "4", "name" to "Human"),
            "powers" to listOf(mapOf("id" to "1", "name" to "test")),
            "publisher" to mapOf("id" to "1", "name" to "test"),
            "real_name" to "test",
            "story_arc_credits" to listOf(mapOf("id" to "1", "name" to "test")),
            "team_enemies" to listOf(mapOf("id" to "1", "name" to "test")),
            "team_friends" to listOf(mapOf("id" to "1", "name" to "test")),
            "teams" to listOf(mapOf("id" to "1", "name" to "test")),
            "volume_credits" to listOf(mapOf("id" to "1", "name" to "test")),
        )
        val expected = CharacterDetails(
            aliases = Aliases(listOf("alias1", "alias2", "alias3")),
            birth = Date(
                GregorianCalendar(2022, 0, 1)
                    .timeInMillis
            ),
            enemies = listOf(CharacterDetails.Enemy(id = 1, name = "test")),
            friends = listOf(CharacterDetails.Friend(id = 1, name = "test")),
            countOfIssueAppearances = 1,
            creators = listOf(CharacterDetails.Creator(id = 1, name = "test")),
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
            firstAppearedInIssue = CharacterDetails.FirstIssueAppearance(
                id = 1,
                name = "test",
                issueNumber = "1",
            ),
            gender = Gender.Male,
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
            issueCredits = listOf(CharacterDetails.Issue(id = 1, name = "test")),
            issuesDiedIn = listOf(CharacterDetails.Issue(id = 1, name = "test")),
            movies = listOf(CharacterDetails.Movie(id = 1, name = "test")),
            name = "test",
            origin = Origin(id = 4, name = "Human"),
            powers = listOf(CharacterDetails.Power(id = 1, name = "test")),
            publisher = CharacterDetails.Publisher(id = 1, name = "test"),
            realName = "test",
            storyArcCredits = listOf(CharacterDetails.StoryArc(id = 1, name = "test")),
            teamEnemies = listOf(CharacterDetails.Team(id = 1, name = "test")),
            teamFriends = listOf(CharacterDetails.Team(id = 1, name = "test")),
            teams = listOf(CharacterDetails.Team(id = 1, name = "test")),
            volumeCredits = listOf(CharacterDetails.Volume(id = 1, name = "test")),
        )
        val moshi = Moshi.Builder()
            .add(EnumJsonAdapterFactory)
            .add(ComicVineDateConverter)
            .add(AliasesConverter)
            .build()
        assertEquals(expected, moshi.parse<CharacterDetails>(json))
    }
}