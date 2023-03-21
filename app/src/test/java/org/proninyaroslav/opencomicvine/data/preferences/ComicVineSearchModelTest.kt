package org.proninyaroslav.opencomicvine.data.preferences

import org.junit.Assert.*
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.ComicVineSearchResourceType

class ComicVineSearchModelTest {
    @Test
    fun toComicVineResourceType() {
        listOf(
            PrefSearchFilter.Resources.Selected(
                resourceTypes = setOf(
                    PrefSearchResourceType.Character,
                    PrefSearchResourceType.Issue,
                )
            ),
            PrefSearchFilter.Resources.All,
            PrefSearchFilter.Resources.Unknown,
        ).onEach {
            when (it) {
                PrefSearchFilter.Resources.All -> {
                    assertEquals(
                        ComicVineSearchResourceType.values().toSet(),
                        it.toComicVineResourceType()
                    )
                }
                is PrefSearchFilter.Resources.Selected -> {
                    assertEquals(
                        setOf(
                            ComicVineSearchResourceType.Character,
                            ComicVineSearchResourceType.Issue,
                        ),
                        it.toComicVineResourceType()
                    )
                }
                PrefSearchFilter.Resources.Unknown -> {
                    assertEquals(
                        emptySet<ComicVineSearchResourceType>(),
                        it.toComicVineResourceType()
                    )
                }
            }
        }
    }
}