package com.dev.sicenet.data

import android.content.Context
import androidx.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor que agrega las cookies guardadas en SharedPreferences a cada Request.
 */
class AddCookiesInterceptor(
    private val context: Context
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val cookies: Set<String>? = preferences.getStringSet(PREF_COOKIES, emptySet())

        cookies?.forEach { cookie ->
            builder.addHeader("Cookie", cookie)
        }

        return chain.proceed(builder.build())
    }

    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
}

