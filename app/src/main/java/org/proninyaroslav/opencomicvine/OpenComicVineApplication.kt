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

package org.proninyaroslav.opencomicvine

import android.app.Application
import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.proninyaroslav.opencomicvine.ui.crash_report.CrashReportActivity

private val TAG = OpenComicVineApplication::class.simpleName

@HiltAndroidApp
class OpenComicVineApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .crossfade(true)
            .build()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        initCrashReport()
    }

    private fun initCrashReport() {
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            mailSender {
                mailTo = "proninyaroslav@mail.ru"
            }
            dialog {
                enabled = true
                reportDialogClass = CrashReportActivity::class.java
            }
        }
        // Set stub handler
        if (Thread.getDefaultUncaughtExceptionHandler() == null) {
            Thread.setDefaultUncaughtExceptionHandler { t: Thread, e: Throwable? ->
                Log.e(
                    TAG,
                    "Uncaught exception in " + t + ": " + Log.getStackTraceString(e)
                )
            }
        }
    }
}
