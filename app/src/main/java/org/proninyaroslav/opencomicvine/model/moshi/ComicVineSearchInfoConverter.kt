package org.proninyaroslav.opencomicvine.model.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.proninyaroslav.opencomicvine.data.ComicVineSearchInfoJson
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceType
import org.proninyaroslav.opencomicvine.data.SearchInfo

object ComicVineSearchInfoConverter {
    @FromJson
    fun fromJson(json: ComicVineSearchInfoJson): SearchInfo {
        return when (json.resource_type) {
            ComicVineSearchResourceType.Character -> json.toCharacter()
            ComicVineSearchResourceType.Concept -> json.toConcept()
            ComicVineSearchResourceType.Object -> json.toObject()
            ComicVineSearchResourceType.Location -> json.toLocation()
            ComicVineSearchResourceType.Issue -> json.toIssue()
            ComicVineSearchResourceType.StoryArc -> json.toStoryArc()
            ComicVineSearchResourceType.Volume -> json.toVolume()
            ComicVineSearchResourceType.Person -> json.toPerson()
            ComicVineSearchResourceType.Team -> json.toTeam()
            ComicVineSearchResourceType.Video -> json.toVideo()
            ComicVineSearchResourceType.Series -> json.toSeries()
            ComicVineSearchResourceType.Episode -> json.toEpisode()
        }
    }

    @ToJson
    fun toJson(info: SearchInfo): ComicVineSearchInfoJson {
        return when (info) {
            is SearchInfo.Character -> info.toJson()
            is SearchInfo.Concept -> info.toJson()
            is SearchInfo.Episode -> info.toJson()
            is SearchInfo.Issue -> info.toJson()
            is SearchInfo.Location -> info.toJson()
            is SearchInfo.Object -> info.toJson()
            is SearchInfo.Person -> info.toJson()
            is SearchInfo.Series -> info.toJson()
            is SearchInfo.StoryArc -> info.toJson()
            is SearchInfo.Team -> info.toJson()
            is SearchInfo.Video -> info.toJson()
            is SearchInfo.Volume -> info.toJson()
        }
    }

    private fun ComicVineSearchInfoJson.toCharacter() = SearchInfo.Character(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
        countOfIssueAppearances = count_of_issue_appearances,
        publisher = publisher?.run {
            SearchInfo.Character.Publisher(
                id = id,
                name = name,
            )
        },
    )

    private fun ComicVineSearchInfoJson.toConcept() = SearchInfo.Concept(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
    )

    private fun ComicVineSearchInfoJson.toObject() = SearchInfo.Object(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
    )

    private fun ComicVineSearchInfoJson.toLocation() = SearchInfo.Location(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
    )

    private fun ComicVineSearchInfoJson.toIssue() = SearchInfo.Issue(
        id = id,
        name = name,
        descriptionShort = deck,
        image = image,
        issueNumber = issue_number,
        coverDate = cover_date,
        volume = volume.run {
            SearchInfo.Issue.Volume(
                id = id,
                name = name,
            )
        },
    )

    private fun ComicVineSearchInfoJson.toStoryArc() = SearchInfo.StoryArc(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
        publisher = publisher?.run {
            SearchInfo.StoryArc.Publisher(
                id = id,
                name = name,
            )
        }
    )

    private fun ComicVineSearchInfoJson.toVolume() = SearchInfo.Volume(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
        publisher = publisher?.run {
            SearchInfo.Volume.Publisher(
                id = id,
                name = name,
            )
        },
        startYear = start_year,
        firstIssue = first_issue?.run {
            SearchInfo.Volume.Issue(
                id = id,
                name = name,
                issueNumber = issue_number,
            )
        },
        lastIssue = last_issue?.run {
            SearchInfo.Volume.Issue(
                id = id,
                name = name,
                issueNumber = issue_number,
            )
        },
        _countOfIssues = count_of_issues,
    )

    private fun ComicVineSearchInfoJson.toPerson() = SearchInfo.Person(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
    )

    private fun ComicVineSearchInfoJson.toTeam() = SearchInfo.Team(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
    )

    private fun ComicVineSearchInfoJson.toVideo() = SearchInfo.Video(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
    )

    private fun ComicVineSearchInfoJson.toSeries() = SearchInfo.Series(
        id = id,
        name = name!!,
        descriptionShort = deck,
        image = image,
        startYear = start_year,
        firstEpisode = first_episode?.run {
            SearchInfo.Series.Episode(
                id = id,
                name = name,
                episodeNumber = episode_number,
            )
        },
        lastEpisode = last_episode?.run {
            SearchInfo.Series.Episode(
                id = id,
                name = name,
                episodeNumber = episode_number,
            )
        },
        _countOfEpisodes = count_of_episodes,
        publisher = publisher?.run {
            SearchInfo.Series.Publisher(
                id = id,
                name = name,
            )
        },
    )

    private fun ComicVineSearchInfoJson.toEpisode() = SearchInfo.Episode(
        id = id,
        name = name,
        descriptionShort = deck,
        image = image,
        episodeNumber = episode_number,
        airDate = air_date,
        series = series.run {
            SearchInfo.Episode.Series(
                id = id,
                name = name,
            )
        }
    )

    private fun SearchInfo.Character.toJson() = ComicVineSearchInfoJson(
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

    private fun SearchInfo.Concept.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Concept,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
    )

    private fun SearchInfo.Object.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Object,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
    )

    private fun SearchInfo.Location.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Location,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
    )

    private fun SearchInfo.Issue.toJson() = ComicVineSearchInfoJson(
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

    private fun SearchInfo.StoryArc.toJson() = ComicVineSearchInfoJson(
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

    private fun SearchInfo.Volume.toJson() = ComicVineSearchInfoJson(
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

    private fun SearchInfo.Person.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Person,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
    )

    private fun SearchInfo.Team.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Team,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
    )

    private fun SearchInfo.Video.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Video,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
    )

    private fun SearchInfo.Series.toJson() = ComicVineSearchInfoJson(
        resource_type = ComicVineSearchResourceType.Series,
        id = id,
        name = name,
        deck = descriptionShort,
        image = image,
        start_year = startYear,
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

    private fun SearchInfo.Episode.toJson() = ComicVineSearchInfoJson(
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