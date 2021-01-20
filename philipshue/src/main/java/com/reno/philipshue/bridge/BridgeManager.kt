package com.reno.philipshue.bridge

import org.koin.java.KoinJavaComponent.inject

class BridgeManager : IBridgeManager {
    private val nUpnpDiscovery: NUPnPDiscovery by inject(NUPnPDiscovery::class.java)

    override suspend fun getBridgeList(): List<Bridge> {
        return nUpnpDiscovery.getBridges()
    }
}

interface IBridgeManager {
    suspend fun getBridgeList(): List<Bridge>
}
