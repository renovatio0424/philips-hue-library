package com.reno.philipshue.injector

import android.content.Context
import com.reno.philipshue.bridge.IUPnpDiscoveryManager
import com.reno.philipshue.bridge.UPnPDiscoveryManager
import com.reno.philipshue.network.BridgeService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injector {
    fun injectBridgeService(baseUrl:String): BridgeService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BridgeService::class.java)

    fun injectUPnPManager(context: Context) : IUPnpDiscoveryManager
     = UPnPDiscoveryManager.Builder(context)
        .build()
}