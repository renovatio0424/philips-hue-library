package com.reno.philipshue.model

data class BridgeConfig(
    val apiVersion: String,
    val bridgeId: String,
    val dataStoreVersion: String,
    val factoryNew: Boolean,
    val mac: String,
    val modelId: String,
    val name: String,
    val replacesBridgeId: Any,
    val starterKitId: String,
    val swVersion: String
)

fun BridgeConfig.convertToBridge(ipAddress:String) = Bridge(this.bridgeId, ipAddress, this.mac, this.name)
