package org.proninyaroslav.opencomicvine.data

import org.junit.Assert.*
import org.junit.Test

class ComicVineSearchResourceTypeTest {
    @Test
    fun from() {
        assertEquals(ComicVineSearchResourceType.Character, ComicVineSearchResourceType.from("character"))
        assertEquals(ComicVineSearchResourceType.Issue, ComicVineSearchResourceType.from("issue"))
        assertEquals(ComicVineSearchResourceType.Concept, ComicVineSearchResourceType.from("concept"))
        assertEquals(ComicVineSearchResourceType.Object, ComicVineSearchResourceType.from("object"))
        assertEquals(ComicVineSearchResourceType.Location, ComicVineSearchResourceType.from("location"))
        assertEquals(ComicVineSearchResourceType.StoryArc, ComicVineSearchResourceType.from("story_arc"))
        assertEquals(ComicVineSearchResourceType.Volume, ComicVineSearchResourceType.from("volume"))
        assertEquals(ComicVineSearchResourceType.Person, ComicVineSearchResourceType.from("person"))
        assertEquals(ComicVineSearchResourceType.Team, ComicVineSearchResourceType.from("team"))
        assertEquals(ComicVineSearchResourceType.Video, ComicVineSearchResourceType.from("video"))
        assertEquals(ComicVineSearchResourceType.Series, ComicVineSearchResourceType.from("series"))
        assertEquals(ComicVineSearchResourceType.Episode, ComicVineSearchResourceType.from("episode"))
    }
}