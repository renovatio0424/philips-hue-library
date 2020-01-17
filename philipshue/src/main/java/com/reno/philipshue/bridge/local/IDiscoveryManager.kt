package com.reno.philipshue.bridge.local

import com.reno.philipshue.model.Bridge
import com.reno.philipshue.model.UPnPDevice

interface IDiscoveryManager {
//    fun discoverDevices(discoverListener: DiscoverListener)
    suspend fun getBridges():List<Bridge>

    interface DiscoverListener {
        fun onStart()
        fun onFoundNewDevice(device: UPnPDevice)
        fun onFinish(devices: HashSet<UPnPDevice>)
        fun onError(exception: Exception)
    }
}