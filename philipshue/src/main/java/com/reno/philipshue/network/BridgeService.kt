package com.reno.philipshue.network

import com.reno.philipshue.model.Bridge
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface BridgeService {
    @GET("")
    suspend fun useUPnP():Bridge
}
