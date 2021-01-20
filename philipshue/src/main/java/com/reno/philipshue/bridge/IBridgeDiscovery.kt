package com.reno.philipshue.bridge

interface IBridgeDiscovery {
    suspend fun getBridges():List<Bridge>
}