package org.proninyaroslav.opencomicvine.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ComicVineUrlBuilderTest {
    @Test
    fun buildUrl() {
        val id = 1
        assertEquals(
            "character",
            "https://comicvine.gamespot.com/character/4005-$id/",
            ComicVineUrlBuilder.character(id),
        )
        assertEquals(
            "issue",
            "https://comicvine.gamespot.com/issue/4000-$id/",
            ComicVineUrlBuilder.issue(id),
        )
        assertEquals(
            "creator",
            "https://comicvine.gamespot.com/creator/4040-$id/",
            ComicVineUrlBuilder.creator(id),
        )
        assertEquals(
            "publisher",
            "https://comicvine.gamespot.com/publisher/4010-$id/",
            ComicVineUrlBuilder.publisher(id),
        )
        assertEquals(
            "movie",
            "https://comicvine.gamespot.com/movie/4025-$id/",
            ComicVineUrlBuilder.movie(id),
        )
        assertEquals(
            "storyArc",
            "https://comicvine.gamespot.com/story_arc/4045-$id/",
            ComicVineUrlBuilder.storyArc(id),
        )
        assertEquals(
            "team",
            "https://comicvine.gamespot.com/team/4060-$id/",
            ComicVineUrlBuilder.team(id),
        )
        assertEquals(
            "person",
            "https://comicvine.gamespot.com/person/4040-$id/",
            ComicVineUrlBuilder.person(id),
        )
        assertEquals(
            "concept",
            "https://comicvine.gamespot.com/concept/4015-$id/",
            ComicVineUrlBuilder.concept(id),
        )
        assertEquals(
            "location",
            "https://comicvine.gamespot.com/location/4020-$id/",
            ComicVineUrlBuilder.location(id),
        )
        assertEquals(
            "object",
            "https://comicvine.gamespot.com/object/4055-$id/",
            ComicVineUrlBuilder.objectUrl(id),
        )
        assertEquals(
            "volume",
            "https://comicvine.gamespot.com/volume/4050-$id/",
            ComicVineUrlBuilder.volume(id),
        )
        assertEquals(
            "episode",
            "https://comicvine.gamespot.com/episode/4070-$id/",
            ComicVineUrlBuilder.episode(id),
        )
        assertEquals(
            "series",
            "https://comicvine.gamespot.com/series/4075-$id/",
            ComicVineUrlBuilder.series(id),
        )
        assertEquals(
            "video",
            "https://comicvine.gamespot.com/videos/video/2300-$id/",
            ComicVineUrlBuilder.video(id),
        )
    }
}