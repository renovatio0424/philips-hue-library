package com.reno.philipshue.bridge

import retrofit2.http.GET

interface NUPnPService {
    @GET("/")
    suspend fun getBridgeAsync(): List<Bridge>
}