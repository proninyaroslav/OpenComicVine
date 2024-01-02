package org.proninyaroslav.opencomicvine.model.moshi

import io.mockk.mockk
import org.junit.Assert.*

import org.junit.Test
import org.proninyaroslav.opencomicvine.types.ComicVineSearchInfoJson
import org.proninyaroslav.opencomicvine.types.ComicVineSearchResourceType
import org.proninyaroslav.opencomicvine.types.ImageInfo
import org.proninyaroslav.opencomicvine.types.SearchInfo
import java.util.*

class ComicVineSearchInfoConverterTest {

    @Test
    fun fromJson() {
        val image = mockk<ImageInfo>()
        listOf(
            SearchInfo.Character(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
                countOfIssueAppearances = 1,
                publisher = SearchInfo.Character.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            SearchInfo.Concept(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
            ),
            SearchInfo.Object(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
            ),
            SearchInfo.Series(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
                startYear = "2023",
                firstEpisode = SearchInfo.Series.Episode(
                    id = 1,
                    name = "name",
                    episodeNumber = "1",
                ),
                lastEpisode = SearchInfo.Series.Episode(
                    id = 1,
                    name = "name",
                    episodeNumber = "2",
                ),
                _countOfEpisodes = 2,
                publisher = SearchInfo.Series.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            SearchInfo.StoryArc(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
                publisher = SearchInfo.StoryArc.Publisher(
                    id = 1,
                    name = "name",
                )
            ),
            SearchInfo.Volume(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
                startYear = "2023",
                firstIssue = SearchInfo.Volume.Issue(
                    id = 1,
                    name = "name",
                    issueNumber = "1",
                ),
                lastIssue = SearchInfo.Volume.Issue(
                    id = 1,
                    name = "name",
                    issueNumber = "2",
                ),
                _countOfIssues = 2,
                publisher = SearchInfo.Volume.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            SearchInfo.Person(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
            ),
            SearchInfo.Team(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
            ),
            SearchInfo.Video(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
            ),
            SearchInfo.Episode(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
                episodeNumber = "1",
                airDate = Date(
                    GregorianCalendar(2023, 0, 1).timeInMillis
                ),
                series = SearchInfo.Episode.Series(
                    id = 1,
                    name = "name",
                ),
            ),
            SearchInfo.Issue(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
                issueNumber = "1",
                coverDate = Date(
                    GregorianCalendar(2023, 0, 1).timeInMillis
                ),
                volume = SearchInfo.Issue.Volume(
                    id = 1,
                    name = "name",
                ),
            ),
            SearchInfo.Location(
                id = 1,
                name = "name",
                descriptionShort = "description",
                image = image,
            ),
        ).onEach {
            when (it) {
                is SearchInfo.Character -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Character,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                            count_of_issue_appearances = 1,
                            publisher = ComicVineSearchInfoJson.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                is SearchInfo.Concept -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Concept,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                        )
                    )
                )
                is SearchInfo.Episode -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Episode,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                            episode_number = "1",
                            air_date = Date(
                                GregorianCalendar(2023, 0, 1).timeInMillis
                            ),
                            series = ComicVineSearchInfoJson.Series(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                is SearchInfo.Issue -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Issue,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                            issue_number = "1",
                            cover_date = Date(
                                GregorianCalendar(2023, 0, 1).timeInMillis
                            ),
                            volume = ComicVineSearchInfoJson.Volume(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                is SearchInfo.Location -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Location,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                        )
                    )
                )
                is SearchInfo.Object -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Object,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                        )
                    )
                )
                is SearchInfo.Person -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Person,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                        )
                    )
                )
                is SearchInfo.Series -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Series,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                            start_year = "2023",
                            first_episode = ComicVineSearchInfoJson.Episode(
                                id = 1,
                                name = "name",
                                episode_number = "1",
                            ),
                            last_episode = ComicVineSearchInfoJson.Episode(
                                id = 1,
                                name = "name",
                                episode_number = "2",
                            ),
                            count_of_episodes = 2,
                            publisher = ComicVineSearchInfoJson.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                is SearchInfo.StoryArc -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.StoryArc,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                            publisher = ComicVineSearchInfoJson.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                is SearchInfo.Team -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Team,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                        )
                    )
                )
                is SearchInfo.Video -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Video,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                        )
                    )
                )
                is SearchInfo.Volume -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.fromJson(
                        ComicVineSearchInfoJson(
                            resource_type = ComicVineSearchResourceType.Volume,
                            id = 1,
                            name = "name",
                            deck = "description",
                            image = image,
                            start_year = "2023",
                            first_issue = ComicVineSearchInfoJson.Issue(
                                id = 1,
                                name = "name",
                                issue_number = "1",
                            ),
                            last_issue = ComicVineSearchInfoJson.Issue(
                                id = 1,
                                name = "name",
                                issue_number = "2",
                            ),
                            count_of_issues = 2,
                            publisher = ComicVineSearchInfoJson.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
            }
        }
    }

    @Test
    fun toJson() {
        val image = mockk<ImageInfo>()
        listOf(
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Character,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
                count_of_issue_appearances = 1,
                publisher = ComicVineSearchInfoJson.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Concept,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Object,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Series,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
                start_year = "2023",
                first_episode = ComicVineSearchInfoJson.Episode(
                    id = 1,
                    name = "name",
                    episode_number = "1",
                ),
                last_episode = ComicVineSearchInfoJson.Episode(
                    id = 1,
                    name = "name",
                    episode_number = "2",
                ),
                count_of_episodes = 2,
                publisher = ComicVineSearchInfoJson.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.StoryArc,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
                publisher = ComicVineSearchInfoJson.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Volume,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
                start_year = "2023",
                first_issue = ComicVineSearchInfoJson.Issue(
                    id = 1,
                    name = "name",
                    issue_number = "1",
                ),
                last_issue = ComicVineSearchInfoJson.Issue(
                    id = 1,
                    name = "name",
                    issue_number = "2",
                ),
                count_of_issues = 2,
                publisher = ComicVineSearchInfoJson.Publisher(
                    id = 1,
                    name = "name",
                ),
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Person,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Team,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Video,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Episode,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
                episode_number = "1",
                air_date = Date(
                    GregorianCalendar(2023, 0, 1).timeInMillis
                ),
                series = ComicVineSearchInfoJson.Series(
                    id = 1,
                    name = "name",
                ),
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Issue,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
                issue_number = "1",
                cover_date = Date(
                    GregorianCalendar(2023, 0, 1).timeInMillis
                ),
                volume = ComicVineSearchInfoJson.Volume(
                    id = 1,
                    name = "name",
                ),
            ),
            ComicVineSearchInfoJson(
                resource_type = ComicVineSearchResourceType.Location,
                id = 1,
                name = "name",
                deck = "description",
                image = image,
            ),
        ).onEach {
            when (it.resource_type) {
                ComicVineSearchResourceType.Character -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Character(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                            countOfIssueAppearances = 1,
                            publisher = SearchInfo.Character.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        ),
                    )
                )
                ComicVineSearchResourceType.Concept -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Concept(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                        )
                    )
                )
                ComicVineSearchResourceType.Object -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Object(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                        )
                    )
                )
                ComicVineSearchResourceType.Location -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Location(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                        )
                    )
                )
                ComicVineSearchResourceType.Issue -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Issue(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                            issueNumber = "1",
                            coverDate = Date(
                                GregorianCalendar(2023, 0, 1).timeInMillis
                            ),
                            volume = SearchInfo.Issue.Volume(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                ComicVineSearchResourceType.StoryArc -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.StoryArc(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                            publisher = SearchInfo.StoryArc.Publisher(
                                id = 1,
                                name = "name",
                            )
                        )
                    )
                )
                ComicVineSearchResourceType.Volume -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Volume(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                            startYear = "2023",
                            firstIssue = SearchInfo.Volume.Issue(
                                id = 1,
                                name = "name",
                                issueNumber = "1",
                            ),
                            lastIssue = SearchInfo.Volume.Issue(
                                id = 1,
                                name = "name",
                                issueNumber = "2",
                            ),
                            _countOfIssues = 2,
                            publisher = SearchInfo.Volume.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                ComicVineSearchResourceType.Person -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Person(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                        )
                    )
                )
                ComicVineSearchResourceType.Team -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Team(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                        )
                    )
                )
                ComicVineSearchResourceType.Video -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Video(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                        )
                    )
                )
                ComicVineSearchResourceType.Series -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Series(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                            startYear = "2023",
                            firstEpisode = SearchInfo.Series.Episode(
                                id = 1,
                                name = "name",
                                episodeNumber = "1",
                            ),
                            lastEpisode = SearchInfo.Series.Episode(
                                id = 1,
                                name = "name",
                                episodeNumber = "2",
                            ),
                            _countOfEpisodes = 2,
                            publisher = SearchInfo.Series.Publisher(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
                ComicVineSearchResourceType.Episode -> assertEquals(
                    it,
                    ComicVineSearchInfoConverter.toJson(
                        SearchInfo.Episode(
                            id = 1,
                            name = "name",
                            descriptionShort = "description",
                            image = image,
                            episodeNumber = "1",
                            airDate = Date(
                                GregorianCalendar(2023, 0, 1).timeInMillis
                            ),
                            series = SearchInfo.Episode.Series(
                                id = 1,
                                name = "name",
                            ),
                        )
                    )
                )
            }
        }
    }
}