package org.proninyaroslav.opencomicvine.ui.crash_report

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import org.acra.ReportField
import org.acra.dialog.CrashReportDialogHelper
import org.proninyaroslav.opencomicvine.ui.AppCompositionLocalProvider

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class CrashReportActivity : AppCompatActivity() {
    private lateinit var helper: CrashReportDialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        helper = CrashReportDialogHelper(this, intent)

        setContent {
            val snackbarState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            AppCompositionLocalProvider(
                activity = this,
                snackbarState = snackbarState,
                coroutineScope = coroutineScope,
            ) {
                val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                CrashReportDialog(
                    stackTrace = helper.reportData.getString(ReportField.STACK_TRACE)!!,
                    onReport = {
                        helper.sendCrash(comment = it.comment, userEmail = null)
                        finish()
                    },
                    onDismissRequest = {
                        helper.cancelReports()
                        finish()
                    },
                    isExpandedWidth = widthSizeClass == WindowWidthSizeClass.Expanded,
                )
            }
        }
    }
}