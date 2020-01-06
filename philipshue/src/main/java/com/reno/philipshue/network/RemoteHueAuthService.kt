package com.reno.philipshue.network

import com.reno.philipshue.bridge.remote.exception.RemoteHueException
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

const val AUTH_BASE_URL: String = "https://us-central1-philipsserver-8d3d1.cloudfunctions.net/"

interface RemoteHueAuthService {
    @GET("barearToken")
    @Throws(RemoteHueException::class)
    fun requestTokenAsync(uuid: String): Deferred<String>
}