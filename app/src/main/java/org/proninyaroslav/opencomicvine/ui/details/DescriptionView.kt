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

package org.proninyaroslav.opencomicvine.ui.details

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView.HitTestResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import org.proninyaroslav.opencomicvine.R
import org.proninyaroslav.opencomicvine.model.COMIC_VINE_BASE_URL
import org.proninyaroslav.opencomicvine.ui.components.list.EmptyListPlaceholder
import org.proninyaroslav.opencomicvine.ui.theme.OpenComicVineTheme
import org.proninyaroslav.opencomicvine.ui.webview.ComicVineWebChromeClient
import org.proninyaroslav.opencomicvine.ui.webview.ComicVineWebViewClient
import org.proninyaroslav.opencomicvine.ui.webview.WebViewThemeProvider

@Composable
fun DescriptionView(
    description: String?,
    loading: Boolean,
    isExpandedWidth: Boolean,
    onLinkClick: (Uri) -> Unit,
    onLinkLongClick: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {

    if (loading) {
        Box {
            DescriptionWebView(
                description = description,
                onLinkClick = onLinkClick,
                onLinkLongClick = onLinkLongClick,
                modifier = modifier,
            )
            Loading(
                isExpandedWidth = isExpandedWidth,
                modifier = modifier,
            )
        }
    } else if (description.isEmptyDescription()) {
        EmptyDescription(modifier = modifier)
    } else {
        DescriptionWebView(
            description = description,
            onLinkClick = onLinkClick,
            onLinkLongClick = onLinkLongClick,
            modifier = modifier,
        )
    }
}


private fun String?.isEmptyDescription(): Boolean =
    isNullOrEmpty() || length <= 10 && isBlank()

@Composable
private fun Loading(
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
    ) {
        val count = 3
        repeat(count) {
            Row {
                DetailsPlaceholderText(
                    null,
                    visible = true,
                    isExpandedWidth = isExpandedWidth,
                    modifier = Modifier.fillMaxWidth((it + 1) / count.toFloat())
                )
            }
        }
    }
}

@Composable
private fun EmptyDescription(modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        EmptyListPlaceholder(
            icon = R.drawable.ic_description_24,
            label = stringResource(R.string.no_description),
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun DescriptionWebView(
    description: String?,
    onLinkClick: (Uri) -> Unit,
    onLinkLongClick: (Uri) -> Unit,
    modifier: Modifier
) {
    val webViewState = rememberWebViewStateWithHTMLData(
        data = description ?: "",
        baseUrl = COMIC_VINE_BASE_URL,
    )

    val colorScheme = MaterialTheme.colorScheme
    val themeProvider = remember(colorScheme) {
        WebViewThemeProvider(colorScheme = colorScheme)
    }
    val client = remember(themeProvider) {
        ComicVineWebViewClient(
            themeProvider = themeProvider,
            onLinkClick = onLinkClick,
        )
    }
    val chromeClient = remember { ComicVineWebChromeClient() }

    WebView(
        state = webViewState,
        captureBackPresses = false,
        onCreated = { webView ->
            webView.settings.apply {
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                javaScriptEnabled = true
            }
            webView.setOnLongClickListener {
                val result = webView.hitTestResult
                when (result.type) {
                    HitTestResult.SRC_ANCHOR_TYPE,
                    HitTestResult.SRC_IMAGE_ANCHOR_TYPE,
                    HitTestResult.IMAGE_TYPE -> {
                        result.extra?.let { onLinkLongClick(it.toUri()) }
                        true
                    }
                    else -> false
                }
            }
        },
        client = client,
        chromeClient = chromeClient,
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(name = "Empty")
@Composable
private fun PreviewDescriptionView_Empty() {
    OpenComicVineTheme {
        DescriptionView(
            description = null,
            loading = false,
            isExpandedWidth = false,
            onLinkClick = {},
            onLinkLongClick = {},
        )
    }
}
