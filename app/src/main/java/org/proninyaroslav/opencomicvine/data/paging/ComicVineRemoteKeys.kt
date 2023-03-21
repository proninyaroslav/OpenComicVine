package org.proninyaroslav.opencomicvine.data.paging

interface ComicVineRemoteKeys {
    val id: Int
    val prevOffset: Int?
    val nextOffset: Int?
}