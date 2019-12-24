package com.reno.philipshue.bridge

import android.content.Context
import android.net.wifi.WifiManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.model.UPnPDevice
import com.reno.philipshue.model.convertToBridge
import com.reno.philipshue.model.isPhillipsHueBridge
import com.reno.philipshue.network.UPnPService
import kotlinx.coroutines.*
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class UPnPDiscoveryManager(
    private val socketDiscoveryManager: SocketDiscoveryManager
) : IUPnPDiscoveryManager {

    override suspend fun getBridges(): List<Bridge> {
        val uPnPDeviceSet = discoverDevices().toList()
        val ipAddress = uPnPDeviceSet.filter { it.isPhillipsHueBridge() }[0].location
        val uPnPService:UPnPService by inject(UPnPService::class.java){
            parametersOf(ipAddress)
        }
        val bridgeConfig = uPnPService.getBridgeConfig().await()

        return arrayListOf(
            bridgeConfig.convertToBridge(ipAddress)
        )
    }

    private suspend fun discoverDevices(): HashSet<UPnPDevice> {
        return socketDiscoveryManager.getDevices()
    }
}

interface IUPnPDiscoveryManager : IDiscoveryManager