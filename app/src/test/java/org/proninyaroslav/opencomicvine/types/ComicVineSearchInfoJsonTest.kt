package org.proninyaroslav.opencomicvine.types

import org.junit.Assert.*
import org.junit.Test
import org.proninyaroslav.opencomicvine.model.moshi.ComicVineSearchInfoConverter
import java.util.*

class ComicVineSearchInfoJsonTest {
    private val converter = ComicVineSearchInfoConverter

    @Test
    fun character() {
        val info = SearchInfo.Character(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Character,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
                count_of_issue_appearances = countOfIssueAppearances,
                publisher = publisher.run {
                    ComicVineSearchInfoJson.Publisher(
                        id = id,
                        name = name,
                    )
                },
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun concept() {
        val info = SearchInfo.Concept(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Concept,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun `object`() {
        val info = SearchInfo.Object(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Object,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun location() {
        val info = SearchInfo.Location(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Location,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun issue() {
        val info = SearchInfo.Issue(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Issue,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
                volume = volume.run {
                    ComicVineSearchInfoJson.Volume(
                        id = id,
                        name = name,
                    )
                },
                issue_number = issueNumber,
                cover_date = coverDate,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun `Story arc`() {
        val info = SearchInfo.StoryArc(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.StoryArc,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
                publisher = publisher.run {
                    ComicVineSearchInfoJson.Publisher(
                        id = id,
                        name = name,
                    )
                },
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun volume() {
        val info = SearchInfo.Volume(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Volume,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
                publisher = publisher.run {
                    ComicVineSearchInfoJson.Publisher(
                        id = id,
                        name = name,
                    )
                },
                start_year = startYear,
                first_issue = firstIssue?.run {
                    ComicVineSearchInfoJson.Issue(
                        id = id,
                        name = name,
                        issue_number = issueNumber,
                    )
                },
                last_issue = lastIssue?.run {
                    ComicVineSearchInfoJson.Issue(
                        id = id,
                        name = name,
                        issue_number = issueNumber,
                    )
                },
                count_of_issues = countOfIssues,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun person() {
        val info = SearchInfo.Person(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Person,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun team() {
        val info = SearchInfo.Team(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Team,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun video() {
        val info = SearchInfo.Video(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Video,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun episode() {
        val info = SearchInfo.Episode(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Episode,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
                episode_number = episodeNumber,
                air_date = airDate,
                series = series.run {
                    ComicVineSearchInfoJson.Series(
                        id = id,
                        name = name,
                    )
                },
            )
        }
        assertEquals(expected, converter.toJson(info))
    }

    @Test
    fun series() {
        val info = SearchInfo.Series(
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
        val expected = info.run {
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Series,
                id = id,
                name = name,
                deck = descriptionShort,
                image = image,
                start_year = "1966",
                first_episode = firstEpisode?.run {
                    ComicVineSearchInfoJson.Episode(
                        id = id,
                        name = name,
                        episode_number = episodeNumber,
                    )
                },
                last_episode = lastEpisode?.run {
                    ComicVineSearchInfoJson.Episode(
                        id = id,
                        name = name,
                        episode_number = episodeNumber,
                    )
                },
                count_of_episodes = countOfEpisodes,
                publisher = publisher?.run {
                    ComicVineSearchInfoJson.Publisher(
                        id = id,
                        name = name,
                    )
                },
            )
        }
        assertEquals(expected, converter.toJson(info))
    }
}