package com.reno.philipshue

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.BridgeService
import retrofit2.Retrofit

class BridgeManager : IBridgeManager {
    val bridgeService = Retrofit.Builder()
        .baseUrl("")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(BridgeService::class.java)

    override fun connectBridge(): Bridge {
        val job = bridgeService.useUPnP()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

interface IBridgeManager {
    fun connectBridge(): Bridge
}
