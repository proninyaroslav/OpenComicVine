package org.proninyaroslav.opencomicvine.ui.search.history

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.data.SearchHistoryInfo
import org.proninyaroslav.opencomicvine.di.IoDispatcher
import org.proninyaroslav.opencomicvine.model.AppPreferences
import org.proninyaroslav.opencomicvine.model.DateProvider
import org.proninyaroslav.opencomicvine.model.repo.SearchHistoryRepository
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchHistoryRepo: SearchHistoryRepository,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val dateProvider: DateProvider,
    private val pref: AppPreferences,
) : StoreViewModel<SearchHistoryEvent, Unit, SearchHistoryEffect>(Unit) {

    val searchHistoryList = searchHistoryRepo.observeAll()
        .onEach(::deleteOldItems)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchHistoryRepository.Result.Success(emptyList()),
        )

    init {
        on<SearchHistoryEvent.AddToHistory> { event ->
            viewModelScope.launch(ioDispatcher) {
                val info = SearchHistoryInfo(
                    query = event.query,
                    date = dateProvider.now,
                )
                when (val res = searchHistoryRepo.insert(info)) {
                    is SearchHistoryRepository.Result.Success ->
                        emitEffect(SearchHistoryEffect.AddedToHistory(info))
                    is SearchHistoryRepository.Result.Failed.IO ->
                        emitEffect(SearchHistoryEffect.AddToHistoryFailed(res))
                }
            }
        }
        on<SearchHistoryEvent.DeleteFromHistory> { event ->
            viewModelScope.launch(ioDispatcher) {
                when (val res = searchHistoryRepo.delete(event.info)) {
                    is SearchHistoryRepository.Result.Success ->
                        emitEffect(SearchHistoryEffect.RemovedFromHistory(event.info))
                    is SearchHistoryRepository.Result.Failed.IO ->
                        emitEffect(SearchHistoryEffect.DeleteFromHistoryFailed(res))
                }
            }
        }
    }

    private fun deleteOldItems(res: SearchHistoryRepository.Result<List<SearchHistoryInfo>>) {
        if (res !is SearchHistoryRepository.Result.Success) {
            return
        }
        val historyList = res.data
        viewModelScope.launch(ioDispatcher) {
            val maxHistorySize = pref.searchHistorySize.first()
            if (historyList.size > maxHistorySize) {
                searchHistoryRepo.deleteList(
                    historyList.subList(maxHistorySize, historyList.size)
                )
            }
        }
    }
}

sealed interface SearchHistoryEvent {
    data class AddToHistory(val query: String) : SearchHistoryEvent

    data class DeleteFromHistory(val info: SearchHistoryInfo) : SearchHistoryEvent
}

sealed interface SearchHistoryEffect {
    data class AddedToHistory(val info: SearchHistoryInfo) : SearchHistoryEffect

    data class AddToHistoryFailed(
        val error: SearchHistoryRepository.Result.Failed
    ) : SearchHistoryEffect

    data class RemovedFromHistory(val info: SearchHistoryInfo) : SearchHistoryEffect

    data class DeleteFromHistoryFailed(
        val error: SearchHistoryRepository.Result.Failed
    ) : SearchHistoryEffect
}
