package org.proninyaroslav.opencomicvine.types

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Assert.*

import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineDateConverter
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineSearchInfoConverter
import java.util.*

class ComicVineSearchInfoTest {
    val moshi: Moshi = Moshi.Builder()
        .add(ComicVineDateConverter)
        .add(ComicVineSearchInfoConverter)
        .build()
    private val adapter: JsonAdapter<SearchInfo> = moshi.adapter(SearchInfo::class.java)

    @Test
    fun character() {
        val json = """
            {
            "count_of_issue_appearances": 22138,
            "deck": "Description",
            "id": 1699,
            "image": {
                "icon_url": "https://example.org/icon.jpg",
                "medium_url": "https://example.org/medium.jpg",
                "screen_url": "https://example.org/screen.jpg",
                "screen_large_url": "https://example.org/screen_large.jpg",
                "small_url": "https://example.org/small.jpg",
                "super_url": "https://example.org/super.jpg",
                "thumb_url": "https://example.org/thumb.jpg",
                "tiny_url": "https://example.org/tiny.jpg",
                "original_url": "https://example.org/original.jpg",
                "image_tags": "All Images"
            },
            "name": "Batman",
            "publisher": {
                "api_detail_url": "https://comicvine.gamespot.com/api/publisher/4010-10/",
                "id": 10,
                "name": "DC Comics"
            },
            "resource_type": "character"
            }
        """.trimIndent()

        val expected = SearchInfo.Character(
            id = 1699,
            name = "Batman",
            descriptionShort = "Description",
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
            countOfIssueAppearances = 22138,
            publisher = SearchInfo.Character.Publisher(
                id = 10,
                name = "DC Comics",
            ),
        )

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun concept() {
        val json = """
            {
                "deck": "Description",
                "id": 55720,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Batman Villains",
                "resource_type": "concept"
            }
        """.trimIndent()

        val expected = SearchInfo.Concept(
            id = 55720,
            name = "Batman Villains",
            descriptionShort = "Description",
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

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun `object`() {
        val json = """
            {
                "deck": "Description",
                "id": 32213,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Kryptonite",
                "resource_type": "object"
            }
        """.trimIndent()

        val expected = SearchInfo.Object(
            id = 32213,
            name = "Kryptonite",
            descriptionShort = "Description",
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

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun location() {
        val json = """
            {
                "deck": "Description",
                "id": 56378,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Moscow",
                "resource_type": "location"
            }
        """.trimIndent()

        val expected = SearchInfo.Location(
            id = 56378,
            name = "Moscow",
            descriptionShort = "Description",
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

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun issue() {
        val json = """
            {
                 "cover_date": "2022-05-01",
                 "deck": "Description",
                 "id": 942768,
                 "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                 "issue_number": "121",
                 "name": "Batman",
                 "volume": {
                     "api_detail_url": "https://comicvine.gamespot.com/api/volume/4050-49597/",
                     "id": 49597,
                     "name": "Batman",
                     "site_detail_url": "https://comicvine.gamespot.com/batman/4050-49597/"
                 },
                 "resource_type": "issue"
          }
        """.trimIndent()

        val expected = SearchInfo.Issue(
            id = 942768,
            name = "Batman",
            descriptionShort = "Description",
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
            issueNumber = "121",
            coverDate = Date(
                GregorianCalendar(2022, 4, 1).timeInMillis
            ),
            volume = SearchInfo.Issue.Volume(
                id = 49597,
                name = "Batman",
            )
        )

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun `Story arc`() {
        val json = """
            {
                "deck": "Description",
                "id": 56286,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Batman Incorporated",
                "publisher": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/publisher/4010-10/",
                    "id": 10,
                    "name": "DC Comics"
                },
                "resource_type": "story_arc"
            }
        """.trimIndent()

        val expected = SearchInfo.StoryArc(
            id = 56286,
            name = "Batman Incorporated",
            descriptionShort = "Description",
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
            publisher = SearchInfo.StoryArc.Publisher(
                id = 10,
                name = "DC Comics",
            )
        )

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun volume() {
        val json = """
            {
                "count_of_issues": 1287,
                "deck": "Description",
                "first_issue": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/issue/4000-787949/",
                    "id": 787949,
                    "name": "El Club del Peligro",
                    "issue_number": "2"
                },
                "id": 126840,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "last_issue": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/issue/4000-930887/",
                    "id": 930887,
                    "name": "La Destrucción Total!",
                    "issue_number": "1301"
                },
                "name": "Batman",
                "publisher": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/publisher/4010-2365/",
                    "id": 2365,
                    "name": "Editorial Novaro"
                },
                "start_year": "1954",
                "resource_type": "volume"
            }
        """.trimIndent()

        val expected = SearchInfo.Volume(
            id = 126840,
            name = "Batman",
            descriptionShort = "Description",
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
            startYear = "1954",
            firstIssue = SearchInfo.Volume.Issue(
                id = 787949,
                name = "El Club del Peligro",
                issueNumber = "2",
            ),
            lastIssue = SearchInfo.Volume.Issue(
                id = 930887,
                name = "La Destrucción Total!",
                issueNumber = "1301",
            ),
            publisher = SearchInfo.Volume.Publisher(
                id = 2365,
                name = "Editorial Novaro",
            ),
            _countOfIssues = 1287,
        )

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun person() {
        val json = """
            {
                "deck": "Description",
                "id": 115286,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Kit Batman",
                "resource_type": "person"
            }
        """.trimIndent()

        val expected = SearchInfo.Person(
            id = 115286,
            name = "Kit Batman",
            descriptionShort = "Description",
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

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun team() {
        val json = """
            {
                "deck": "Description",
                "id": 57839,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Batman Inc.",
                "resource_type": "team"
            }
        """.trimIndent()

        val expected = SearchInfo.Team(
            id = 57839,
            name = "Batman Inc.",
            descriptionShort = "Description",
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

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun video() {
        val json = """
            {
                "deck": "Description",
                "id": 3222,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "name": "Andy Kubert Batman Unboxing",
                "resource_type": "video"
            }
        """.trimIndent()

        val expected = SearchInfo.Video(
            id = 3222,
            name = "Andy Kubert Batman Unboxing",
            descriptionShort = "Description",
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

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun episode() {
        val json = """
            {
                "air_date": "2007-09-22",
                "deck": "Description",
                "id": 4227,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "episode_number": "501",
                "name": "The Batman/ Superman Story",
                "series": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/series/4075-38/",
                    "id": 38,
                    "name": "The Batman",
                    "site_detail_url": "https://comicvine.gamespot.com/the-batman/4075-38/"
                },
                "resource_type": "episode"
            }
        """.trimIndent()

        val expected = SearchInfo.Episode(
            id = 4227,
            name = "The Batman/ Superman Story",
            descriptionShort = "Description",
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
            episodeNumber = "501",
            airDate = Date(
                GregorianCalendar(2007, 8, 22).timeInMillis
            ),
            series = SearchInfo.Episode.Series(
                id = 38,
                name = "The Batman",
            )
        )

        assertEquals(expected, adapter.fromJson(json))
    }

    @Test
    fun series() {
        val json = """
            {
                "count_of_episodes": 120,
                "deck": "Description",
                "first_episode": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/episode/4070-561/",
                    "id": 561,
                    "name": "Smack in the Middle",
                    "episode_number": "102"
                },
                "id": 31,
                "image": {
                    "icon_url": "https://example.org/icon.jpg",
                    "medium_url": "https://example.org/medium.jpg",
                    "screen_url": "https://example.org/screen.jpg",
                    "screen_large_url": "https://example.org/screen_large.jpg",
                    "small_url": "https://example.org/small.jpg",
                    "super_url": "https://example.org/super.jpg",
                    "thumb_url": "https://example.org/thumb.jpg",
                    "tiny_url": "https://example.org/tiny.jpg",
                    "original_url": "https://example.org/original.jpg",
                    "image_tags": "All Images"
                },
                "last_episode": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/episode/4070-13966/",
                    "id": 13966,
                    "name": "\"Minerva, Mayhem and Millionaires\"",
                    "episode_number": "326"
                },
                "name": "Batman",
                "publisher": {
                    "api_detail_url": "https://comicvine.gamespot.com/api/publisher/4010-10/",
                    "id": 10,
                    "name": "DC Comics"
                },
                "start_year": "1966",
                "resource_type": "series"
            }
        """.trimIndent()

        val expected = SearchInfo.Series(
            id = 31,
            name = "Batman",
            descriptionShort = "Description",
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
            startYear = "1966",
            firstEpisode = SearchInfo.Series.Episode(
                id = 561,
                name = "Smack in the Middle",
                episodeNumber = "102",
            ),
            lastEpisode = SearchInfo.Series.Episode(
                id = 13966,
                name = "\"Minerva, Mayhem and Millionaires\"",
                episodeNumber = "326",
            ),
            _countOfEpisodes = 120,
            publisher = SearchInfo.Series.Publisher(
                id = 10,
                name = "DC Comics",
            )
        )

        assertEquals(expected, adapter.fromJson(json))
    }
}