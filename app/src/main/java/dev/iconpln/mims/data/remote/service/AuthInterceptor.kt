package dev.iconpln.mims.data.remote.service

import android.content.Context
import android.util.Log
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(context: Context) : Interceptor {
    val ctx = context

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val requestBuilder = chain.request().newBuilder()

            val token = SharedPrefsUtils.getStringPreference(ctx, Config.KEY_JWT, "")
            val tokenWeb = SharedPrefsUtils.getStringPreference(ctx, Config.KEY_JWT_WEB, "")
            requestBuilder.addHeader(Config.KEY_JWT, "$token")
            requestBuilder.addHeader(Config.KEY_AUTHORIZATION, "Bearer $tokenWeb")

            Log.d("AuthInterceptor", "token : $token")

            return chain.proceed(requestBuilder.build())
        } catch (e: Exception) {
            throw e
        }
    }
}