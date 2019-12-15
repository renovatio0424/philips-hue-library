package com.reno.philipshue.network

import com.reno.philipshue.model.BridgeConfig
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface UPnPService {

    @GET("/api/config")
    fun getBridgeConfig(): Deferred<BridgeConfig>

    @GET("/api/{userName}/config")
    fun getBridgeConfig(@Path("userName") userName: String): Deferred<BridgeConfig>
}