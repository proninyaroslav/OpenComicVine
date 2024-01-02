package org.proninyaroslav.opencomicvine.model.network

import android.content.Context
import android.webkit.WebSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface UserAgentProvider {
    fun getSystemUserAgent(): String
}

const val FALLBACK_USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.1; en-us; DV Build/Donut)"

class UserAgentProviderImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : UserAgentProvider {
    override fun getSystemUserAgent(): String = try {
        WebSettings.getDefaultUserAgent(context)
    } catch (e: UnsupportedOperationException) {
        /* Fallback user agent if WebView doesn't supported */
        FALLBACK_USER_AGENT
    }
}