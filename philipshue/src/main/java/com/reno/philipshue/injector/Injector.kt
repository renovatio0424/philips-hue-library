package com.reno.philipshue.injector

import android.content.Context
import com.reno.philipshue.bridge.IDiscoveryManager
import com.reno.philipshue.bridge.NUPnPDiscoveryManager
import com.reno.philipshue.bridge.UPnPDiscoveryManager
import com.reno.philipshue.network.BridgeService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injector {
    fun <T> injectRetrofitService(baseUrl: String, clazz: Class<T>): T = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(clazz)

    fun injectBridgeService(baseUrl: String): BridgeService =
        injectRetrofitService(baseUrl, BridgeService::class.java)

    fun injectUPnPManager(context: Context): IDiscoveryManager =
        UPnPDiscoveryManager.Builder(context)
            .build()

    fun injectNUPnPManager(): IDiscoveryManager = NUPnPDiscoveryManager()
}