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