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

package org.proninyaroslav.opencomicvine.ui.details.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.types.ErrorReportInfo
import org.proninyaroslav.opencomicvine.types.item.BaseItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import java.io.IOException

abstract class DetailsViewModel<DetailsItem : BaseItem, RelatedEntities : DetailsViewModel.RelatedEntities>(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorReportService: ErrorReportService,
) : ViewModel() {

    private val _state = MutableStateFlow<DetailsState<DetailsItem, RelatedEntities>>(
        DetailsState.Initial
    )
    val state: StateFlow<DetailsState<DetailsItem, RelatedEntities>> = _state

    fun load(id: Int) {
        if (isLoadNotAllowed()) {
            return
        }
        _state.value = DetailsState.Loading
        viewModelScope.launch(ioDispatcher) {
            loadCache(id)
        }
        viewModelScope.launch {
            loadRemote(id)
        }
    }

    fun errorReport(info: ErrorReportInfo) {
        errorReportService.report(info)
    }

    protected abstract suspend fun onLoadRemote(entityId: Int): RemoteFetchResult<DetailsItem, RelatedEntities>

    protected abstract suspend fun onLoadCache(entityId: Int): CacheFetchResult<DetailsItem?>

    fun <T> toSourceError(state: LoadState): T? =
        ComicVineSource.stateToError(state)

    private fun isLoadNotAllowed() = state.value.run {
        when (this) {
            DetailsState.Initial,
            is DetailsState.CacheLoaded,
            is DetailsState.CacheLoadFailed,
            is DetailsState.LoadFailed -> false

            is DetailsState.Loaded,
            DetailsState.Loading -> true
        }
    }

    private suspend fun loadCache(id: Int) {
        when (val res = onLoadCache(id)) {
            is CacheFetchResult.Success -> res.details?.let {
                _state.value = DetailsState.CacheLoaded(details = it)
            }

            is CacheFetchResult.Failed.IO -> {
                _state.value = DetailsState.CacheLoadFailed(
                    details = null,
                    exception = res.exception,
                )
            }
        }
    }

    private suspend fun loadRemote(id: Int) {
        when (val res = onLoadRemote(id)) {
            is RemoteFetchResult.Success -> res.run {
                _state.value = DetailsState.Loaded(
                    details = details,
                    relatedEntities = relatedEntities,
                )
            }

            is RemoteFetchResult.Failed -> {
                val current = state.value
                _state.value = DetailsState.LoadFailed(
                    details = when (current) {
                        is DetailsState.CacheLoaded -> current.details
                        is DetailsState.Loaded -> current.details
                        is DetailsState.LoadFailed -> current.details
                        else -> null
                    },
                    error = res.error,
                )
            }
        }
    }

    interface RelatedEntities

    protected sealed interface RemoteFetchResult<out D : BaseItem, out R : RelatedEntities> {
        data class Success<D : BaseItem, R : RelatedEntities>(
            val details: D,
            val relatedEntities: R,
        ) : RemoteFetchResult<D, R>

        data class Failed(
            val error: ComicVineResult.Failed,
        ) : RemoteFetchResult<Nothing, Nothing>
    }

    protected sealed interface CacheFetchResult<out D : BaseItem?> {
        data class Success<D : BaseItem?>(
            val details: D?,
        ) : CacheFetchResult<D>

        sealed interface Failed : CacheFetchResult<Nothing> {
            data class IO(val exception: IOException) : Failed
        }
    }
}

sealed interface DetailsState<out D : BaseItem, out R : DetailsViewModel.RelatedEntities> {
    object Initial : DetailsState<Nothing, Nothing>

    object Loading : DetailsState<Nothing, Nothing>

    data class CacheLoaded<D : BaseItem, R : DetailsViewModel.RelatedEntities>(
        val details: D,
    ) : DetailsState<D, R>

    data class Loaded<D : BaseItem, R : DetailsViewModel.RelatedEntities>(
        val details: D,
        val relatedEntities: R,
    ) : DetailsState<D, R>

    data class LoadFailed<D : BaseItem, R : DetailsViewModel.RelatedEntities>(
        val details: D?,
        val error: ComicVineResult.Failed,
    ) : DetailsState<D, R>

    data class CacheLoadFailed<D : BaseItem, R : DetailsViewModel.RelatedEntities>(
        val details: D?,
        val exception: IOException,
    ) : DetailsState<D, R>
}