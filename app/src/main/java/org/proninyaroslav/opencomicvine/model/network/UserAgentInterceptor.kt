package org.proninyaroslav.opencomicvine.model.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UserAgentInterceptor @Inject constructor(
    private val userAgentProvider: UserAgentProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("user-agent", userAgentProvider.getSystemUserAgent())
                .build()
        )
    }
}