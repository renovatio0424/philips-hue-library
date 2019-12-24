package com.reno.philipshue.bridge.local

import com.reno.philipshue.injector.Injector
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.NUPnPService

const val timeOut: Long = 1000L

class NUPnPDiscoveryManager :
    IDiscoveryManager {
    private val baseUrl: String = "https://discovery.meethue.com/"
    private val nUPnPService: NUPnPService =
        Injector.injectRetrofitService(baseUrl, NUPnPService::class.java)

    override suspend fun getBridges(): List<Bridge> {
        return  nUPnPService.getBridgeAsync()
    }
}