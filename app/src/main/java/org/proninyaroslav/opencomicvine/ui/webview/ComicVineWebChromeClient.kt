package org.proninyaroslav.opencomicvine.ui.webview

import android.util.Log
import android.webkit.ConsoleMessage
import com.google.accompanist.web.AccompanistWebChromeClient

class ComicVineWebChromeClient : AccompanistWebChromeClient() {
    private val tag = this::class.simpleName

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.d(
            tag,
            "${consoleMessage?.message()} -- From line " +
                    "${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}"
        )
        return true
    }
}