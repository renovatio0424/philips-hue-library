package com.reno.philipshue.bridge.local

import com.reno.philipshue.model.Bridge

class LocalBridgeManager(
    private val nuPnPDiscoveryManager: NUPnPDiscoveryManager,
    private val uPnPDiscoveryManager: UPnPDiscoveryManager
) : ILocalBridgeManager {
    override suspend fun getBridgeAsync(): List<Bridge> {
        return uPnPDiscoveryManager.getBridges()
    }
}

interface ILocalBridgeManager {
    suspend fun getBridgeAsync(): List<Bridge>
}

