package com.reno.philipshue.bridge.local

import android.content.Context
import android.net.wifi.WifiManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.reno.philipshue.model.UPnPDevice
import kotlinx.coroutines.*
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

const val DEFAULT_TIME_OUT = 5000L
const val LINE_END = "\r\n"
const val DEFAULT_QUERY = "M-SEARCH * HTTP/1.1$LINE_END" +
        "HOST: 239.255.255.250:1900$LINE_END" +
        "MAN: \"ssdp:discover\"$LINE_END" +
        "MX: 1$LINE_END" +
        //"ST: urn:schemas-upnp-org:service:AVTransport:1$lineEnd" + // Use for Sonos
        //"ST: urn:schemas-upnp-org:device:InternetGatewayDevice:1$lineEnd" + // Use for Routes
        "ST: ssdp:all$LINE_END" +  // Use this for all UPnP Devic$lineEnd"
        LINE_END
const val DEFAULT_ADDRESS = "239.255.255.250"
const val DEFAULT_PORT = 1900

class SocketDiscoveryManager(
    private val context: Context,
    private val timeOut: Long,
    private val customQuery: String,
    private val internetAddress: String,
    private val port: Int
) : ISocketDiscoveryManager {
    private var threadCount = 0
    private val devices = hashSetOf<UPnPDevice>()
    private val wifiManager:WifiManager? by lazy { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }
    data class Builder(
        var context: Context,
        var timeOut: Long = DEFAULT_TIME_OUT,
        var query: String = DEFAULT_QUERY,
        var address: String = DEFAULT_ADDRESS,
        var port: Int = DEFAULT_PORT
    ) {
        fun timeOut(timeOut: Long?) = apply { this.timeOut }
        fun query(query: String?) = apply { this.query }
        fun address(address: String?) = apply { this.address }
        fun port(port: Int?) = apply { this.port }
        fun build() = SocketDiscoveryManager(
            context,
            timeOut,
            query,
            address,
            port
        )
    }

    override suspend fun getDevices(): List<UPnPDevice> {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(com.reno.philipshue.bridge.local.timeOut) {
                    if (wifiManager == null)
                        return@withTimeout

                    val multiCastLock = wifiManager!!.createMulticastLock(internetAddress)
                    multiCastLock.acquire()
                    var socket: DatagramSocket? = null

                    try {
                        val group =
                            InetAddress.getByName(internetAddress)
                        val port: Int = port
                        val query: String = customQuery

                        socket = DatagramSocket(null).apply {
                            reuseAddress = true
                            broadcast = true
                        }

                        socket.bind(InetSocketAddress(port))

                        val datagramPacketRequest =
                            DatagramPacket(query.toByteArray(), query.length, group, port)
                        socket.send(datagramPacketRequest)

                        val time = System.currentTimeMillis()
                        var curTime = System.currentTimeMillis()

                        while (curTime - time < 1000) {
                            val datagramPacket =
                                DatagramPacket(ByteArray(1024), 1024)
                            socket.receive(datagramPacket)
                            val response =
                                String(datagramPacket.data, 0, datagramPacket.length)
                            if (response.substring(0, 12).toUpperCase() == "HTTP/1.1 200") {
                                val device =
                                    UPnPDevice(datagramPacket.address.hostAddress, response)
                                threadCount++
                                updateDeviceSet(device.location, device)
                            }
                            curTime = System.currentTimeMillis()
                        }
                    } catch (e: IOException) {
                        throw e
                    } finally {
                        socket?.close()
                    }
                    multiCastLock.release()
                }
            } catch (exception: Exception) {
                throw exception
            }
            devices.toList()
        }
    }

    private fun updateDeviceSet(
        url: String,
        device: UPnPDevice
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String?> { response ->
                CoroutineScope(Dispatchers.IO).launch {
                    device.update(response)

                    devices.add(device)
                    threadCount--
                }
            }, Response.ErrorListener {
                CoroutineScope(Dispatchers.IO).launch {
                    threadCount--
                }
            })
        Volley.newRequestQueue(context).add(stringRequest)
    }
}

interface ISocketDiscoveryManager {
    suspend fun getDevices(): List<UPnPDevice>
}