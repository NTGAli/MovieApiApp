package com.ntg.movieapiapp.data.remote

import com.ntg.movieapiapp.util.Constants.API.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizeInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $API_KEY")
            .build()
        return chain.proceed(request)
    }

}