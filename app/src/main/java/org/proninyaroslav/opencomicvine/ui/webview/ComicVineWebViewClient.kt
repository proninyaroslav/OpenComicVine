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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.core.net.toUri
import coil.executeBlocking
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.accompanist.web.AccompanistWebViewClient
import org.proninyaroslav.opencomicvine.ui.getBitmapInputStream
import org.proninyaroslav.opencomicvine.ui.getCompressFormatByImageType
import org.proninyaroslav.opencomicvine.ui.getMimeType

class ComicVineWebViewClient(
    private val onLinkClick: (Uri) -> Unit,
    private val themeProvider: WebViewThemeProvider,
) : AccompanistWebViewClient() {
    private val tag = this::class.simpleName

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)

        view.run {
            loadLazyContent()
            loadCss(themeProvider.build())
        }
    }

    private fun WebView.loadLazyContent() {
        val js = """javascript:(function() {
                var items = document.getElementsByClassName('js-lazy-load-image');
                for (var i = 0; i < items.length; i++) {
                    items[i].src = items[i].getAttribute('data-src')
                }
            })()
        """.trimIndent()

        loadUrl(js)
    }

    private fun WebView.loadCss(css: String) {
        val js = """javascript:(function() {
                var node = document.createElement('style');
                node.type = 'text/css';
                node.innerHTML = '${css}';
                document.head.appendChild(node);
            })()
        """.trimIndent()

        loadUrl(js)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        if (request == null || view == null) {
            return super.shouldInterceptRequest(view, request)
        }

        return request.url.isImageUrl()?.let {
            loadImageBlocking(
                context = view.context,
                url = request.url,
                compressFormat = it,
            )
        } ?: super.shouldInterceptRequest(view, request)
    }

    private fun Uri.isImageUrl(): Bitmap.CompressFormat? = getCompressFormatByImageType()

    private fun loadImageBlocking(
        context: Context,
        url: Uri,
        compressFormat: Bitmap.CompressFormat,
    ): WebResourceResponse? {
        val loader = context.imageLoader
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        return when (val result = loader.executeBlocking(request)) {
            is ErrorResult -> null
            is SuccessResult -> {
                val bitmap = (result.drawable as BitmapDrawable).bitmap
                val mimeType = compressFormat.getMimeType()
                WebResourceResponse(
                    mimeType,
                    "UTF-8",
                    bitmap.getBitmapInputStream(compressFormat)
                )
            }
        }
    }

    @Deprecated("Deprecated but used by old Android versions")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return url?.let {
            onLinkClick(url.toUri())
            true
        } ?: false
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return try {
            request?.url?.let {
                onLinkClick(it)
                true
            } ?: false
        } catch (e: NullPointerException) {
            // getUrl can throw NullPointerException
            true
        }
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)

        error?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(tag, "Error while loading page: ${error.errorCode} ${error.description}")
            }
        }
    }
}
