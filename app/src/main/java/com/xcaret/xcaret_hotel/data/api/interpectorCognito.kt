package com.xcaret.xcaret_hotel.data.api

import okhttp3.Credentials
import okhttp3.Interceptor

class interpectorCognito (username: String, password: String): Interceptor {

        private var credentials: String = Credentials.basic(username, password)

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request = request.newBuilder().header("Authorization", credentials).build()
            return chain.proceed(request)
        }
    }