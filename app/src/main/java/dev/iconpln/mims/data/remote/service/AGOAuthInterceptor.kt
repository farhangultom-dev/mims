package dev.iconpln.mims.data.remote.service

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AGOAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder().apply {
            addHeader("Auth-Token", "null|null")
        }
        return chain.proceed(requestBuilder.build())
    }

}