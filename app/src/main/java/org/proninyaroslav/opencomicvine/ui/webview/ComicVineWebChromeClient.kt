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
