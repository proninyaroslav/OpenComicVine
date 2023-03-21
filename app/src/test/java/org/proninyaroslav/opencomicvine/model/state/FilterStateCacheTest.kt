package org.proninyaroslav.opencomicvine.model.state

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterStateCacheTest {
    private lateinit var stateCache: FilterStateCache<Sort, Filter>

    @Before
    fun setUp() {
        stateCache = FilterStateCache(
            FilterStateCache.State(
                sort = Sort.Unknown,
                filter = Filter.Unknown,
            )
        )
    }

    @Test
    fun save() {
        stateCache.save(
            FilterStateCache.State(
                sort = Sort.Sort1,
                filter = Filter.Filter1,
            )
        )
        assertEquals(
            FilterStateCache.State(
                sort = Sort.Sort1,
                filter = Filter.Filter1,
            ),
            stateCache.current
        )

        stateCache.save(
            FilterStateCache.State(
                sort = Sort.Sort1,
                filter = Filter.Filter2,
            )
        )
        assertEquals(
            FilterStateCache.State(
                sort = Sort.Sort1,
                filter = Filter.Filter2,
            ),
            stateCache.current
        )

        stateCache.save(
            FilterStateCache.State(
                sort = Sort.Sort2,
                filter = Filter.Filter1,
            )
        )
        assertEquals(
            FilterStateCache.State(
                sort = Sort.Sort2,
                filter = Filter.Filter1,
            ),
            stateCache.current
        )
    }

    sealed interface Sort {
        object Unknown : Sort
        object Sort1 : Sort
        object Sort2 : Sort
    }

    sealed interface Filter {
        object Unknown : Filter
        object Filter1 : Filter
        object Filter2 : Filter
    }
}