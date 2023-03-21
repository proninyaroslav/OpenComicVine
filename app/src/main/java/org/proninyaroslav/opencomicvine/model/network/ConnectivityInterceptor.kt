package org.proninyaroslav.opencomicvine.model.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    private val connectivityManager: AppConnectivityManager,
) : Interceptor {

    @Throws(NoNetworkConnectionException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityManager.isNetworkAvailable()) {
            throw NoNetworkConnectionException()
        }
        return chain.proceed(chain.request())
    }
}