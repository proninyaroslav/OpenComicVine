package org.proninyaroslav.opencomicvine.ui.details.category

import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.data.item.BaseItem
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.paging.ComicVineSource
import org.proninyaroslav.opencomicvine.model.repo.ComicVineResult
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import java.io.IOException

abstract class DetailsViewModel<DetailsItem : BaseItem, RelatedEntities : DetailsViewModel.RelatedEntities>(
    ioDispatcher: CoroutineDispatcher,
    private val errorReportService: ErrorReportService,
) : StoreViewModel<DetailsEvent, DetailsState<DetailsItem, RelatedEntities>, DetailsEffect>(
    DetailsState.Initial
) {
    init {
        on<DetailsEvent.Load> { event ->
            if (isLoadNotAllowed()) {
                return@on
            }
            emitState(DetailsState.Loading)
            viewModelScope.launch(ioDispatcher) {
                loadCache(event.id)
            }
            viewModelScope.launch {
                loadRemote(event.id)
            }
        }

        on<DetailsEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    protected abstract suspend fun onLoadRemote(entityId: Int): RemoteFetchResult<DetailsItem, RelatedEntities>

    protected abstract suspend fun onLoadCache(entityId: Int): CacheFetchResult<DetailsItem?>

    fun <T> toSourceError(state: LoadState): T? =
        ComicVineSource.stateToError(state)

    private fun isLoadNotAllowed() = state.value.run {
        when (this) {
            DetailsState.Initial,
            is DetailsState.CacheLoaded,
            is DetailsState.LoadFailed -> false
            is DetailsState.Loaded,
            DetailsState.Loading -> true
        }
    }

    private suspend fun loadCache(id: Int) {
        when (val res = onLoadCache(id)) {
            is CacheFetchResult.Success -> res.details?.let {
                emitState(DetailsState.CacheLoaded(details = it))
            }
            is CacheFetchResult.Failed.IO -> emitEffect(
                DetailsEffect.CacheLoadFailed(
                    exception = res.exception,
                )
            )
        }
    }

    private suspend fun loadRemote(id: Int) {
        when (val res = onLoadRemote(id)) {
            is RemoteFetchResult.Success -> res.run {
                emitState(
                    DetailsState.Loaded(
                        details = details,
                        relatedEntities = relatedEntities,
                    )
                )
            }
            is RemoteFetchResult.Failed -> {
                val current = state.value
                emitState(
                    DetailsState.LoadFailed(
                        details = when (current) {
                            is DetailsState.CacheLoaded -> current.details
                            is DetailsState.Loaded -> current.details
                            is DetailsState.LoadFailed -> current.details
                            else -> null
                        },
                        error = res.error,
                    )
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

sealed interface DetailsEvent {
    data class Load(val id: Int) : DetailsEvent

    data class ErrorReport(val info: ErrorReportInfo) : DetailsEvent
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
}

sealed interface DetailsEffect {
    data class CacheLoadFailed(
        val exception: IOException,
    ) : DetailsEffect
}