package org.proninyaroslav.opencomicvine.ui.webview

import androidx.compose.material3.ColorScheme
import org.proninyaroslav.opencomicvine.ui.toHex

class WebViewThemeProvider(private val colorScheme: ColorScheme) {
    fun build(): String =
        """
        body {
            color: ${colorScheme.onSurface.toHex()};
            background-color: ${colorScheme.surface.toHex()};
        }
        a:link {
            color: ${colorScheme.primary.toHex()};
            text-decoration: none;
        }
        a:visited {
            color: ${colorScheme.primary.toHex()};
            text-decoration: none;
        }
    """.trimIndent()
}