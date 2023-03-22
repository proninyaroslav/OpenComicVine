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
