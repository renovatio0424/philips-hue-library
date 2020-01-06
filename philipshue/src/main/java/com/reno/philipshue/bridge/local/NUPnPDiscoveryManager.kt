package com.reno.philipshue.bridge

import com.reno.philipshue.bridge.local.IDiscoveryManager
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.NUPnPService
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

const val timeOut: Long = 1000L

class NUPnPDiscoveryManager : INUPnPDiscoveryManager {
    private val baseUrl: String = "https://discovery.meethue.com/"
    private val nUPnPService: NUPnPService by inject(NUPnPService::class.java) {
        parametersOf(
            baseUrl
        )
    }

    override suspend fun getBridges(): List<Bridge> {
        return nUPnPService.getBridgeAsync()
    }
}

interface INUPnPDiscoveryManager: IDiscoveryManager