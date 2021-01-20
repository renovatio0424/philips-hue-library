package com.reno.philipshue.bridge

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

class NUPnPDiscovery : IBridgeDiscovery {
    private val baseUrl: String = "https://discovery.meethue.com/"
    private val nUPnPService: NUPnPService by inject(NUPnPService::class.java) {
        parametersOf(
            baseUrl
        )
    }

    override suspend fun getBridges(): List<Bridge> {
        return withContext(Dispatchers.IO) {
            nUPnPService.getBridgeAsync()
        }
    }
}