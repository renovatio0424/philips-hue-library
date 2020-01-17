package com.reno.philipshue.bridge.local

import com.reno.philipshue.model.Bridge

class LocalBridgeDiscovery(
    private val nUPnPDiscovery: NUPnPDiscovery,
    private val uPnPDiscovery: UPnPDiscovery
) : ILocalBridgeDiscovery {
    override suspend fun getBridgeAsync(): List<Bridge> {
        return uPnPDiscovery.getBridges()
    }
}

interface ILocalBridgeDiscovery {
    suspend fun getBridgeAsync(): List<Bridge>
}

