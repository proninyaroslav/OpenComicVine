/*
 * Copyright (C) 2023 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of OpenComicVine.
 *
 * OpenComicVine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenComicVine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenComicVine.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import org.proninyaroslav.opencomicvine.types.paging.ComicVinePagingItem
import org.proninyaroslav.opencomicvine.types.paging.ComicVineRemoteKeys
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository

interface FavoritesPagingRepository<Item : ComicVinePagingItem, RemoteKey : ComicVineRemoteKeys> :
    ComicVinePagingRepository<Item, RemoteKey> {

    suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit>
}
