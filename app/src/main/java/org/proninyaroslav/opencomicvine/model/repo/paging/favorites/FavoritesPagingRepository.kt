package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import org.proninyaroslav.opencomicvine.data.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.data.paging.ComicVineRemoteKeys
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository

interface FavoritesPagingRepository<Item : ComicVinePagingItem, RemoteKey : ComicVineRemoteKeys> :
    ComicVinePagingRepository<Item, RemoteKey> {

    suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit>
}