package com.reno.philipshue.bridge

import android.util.Log
import com.reno.philipshue.bridge.local.IDiscoveryManager
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.model.UPnPDevice
import com.reno.philipshue.model.convertToBridge
import com.reno.philipshue.model.isPhillipsHueBridge
import com.reno.philipshue.network.UPnPService
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

const val TAG = "UPnPDiscoveryManager"

class UPnPDiscoveryManager(
    private val socketDiscoveryManager: ISocketDiscoveryManager
) : IUPnPDiscoveryManager {

    override suspend fun getBridges(): List<Bridge> {
        val philipsDevices = discoverDevices().filter { it.isPhillipsHueBridge() }
        return discoverBridges(philipsDevices)
    }

    private suspend fun discoverBridges(uPnPDevices: List<UPnPDevice>): List<Bridge> {
        val bridges = arrayListOf<Bridge>()

        if(uPnPDevices.isNullOrEmpty())
            return bridges

        uPnPDevices.forEach {
            val ipAddress = it.location

            val uPnPService by inject(UPnPService::class.java) {
                parametersOf(ipAddress)
            }
            Log.d(TAG, "ipAddress: $ipAddress")

            val bridgeConfig = uPnPService.getBridgeConfigAsync().await()
            bridges.add(bridgeConfig.convertToBridge(ipAddress))
        }

        return bridges
    }

    private suspend fun discoverDevices(): List<UPnPDevice> {
        return socketDiscoveryManager.getDevices()
    }
}

interface IUPnPDiscoveryManager : IDiscoveryManager