package com.reno.philipshue.network

import com.reno.philipshue.model.Bridge
import retrofit2.http.GET

interface NUPnPService {
    @GET("/")
    suspend fun getBridgeAsync(): List<Bridge>
}