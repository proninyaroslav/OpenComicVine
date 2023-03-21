package org.proninyaroslav.opencomicvine.ui.about

import dagger.hilt.android.lifecycle.HiltViewModel
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.AppInfoProvider
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.state.StoreViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val errorReportService: ErrorReportService,
    appInfoProvider: AppInfoProvider,
) : StoreViewModel<AboutEvent, AboutState, Unit>(load(appInfoProvider)) {

    init {
        on<AboutEvent.ErrorReport> { event ->
            errorReportService.report(event.info)
        }
    }

    companion object {
        private fun load(appInfoProvider: AppInfoProvider): AboutState {
            return when (val res = appInfoProvider.getAppInfo()) {
                is AppInfoProvider.State.Success -> res.run {
                    AboutState.Loaded(
                        appName = appName,
                        version = version,
                    )
                }
                is AppInfoProvider.State.Failed -> AboutState.LoadFailed(res)
            }
        }
    }
}

sealed interface AboutEvent {
    data class ErrorReport(val info: ErrorReportInfo) : AboutEvent
}

sealed interface AboutState {
    data class Loaded(
        val appName: String,
        val version: String,
    ) : AboutState

    data class LoadFailed(val error: AppInfoProvider.State.Failed) : AboutState
}
