package com.reno.philipshue.bridge.local

import android.content.Context
import android.net.wifi.WifiManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.reno.philipshue.injector.Injector
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.model.UPnPDevice
import com.reno.philipshue.model.convertToBridge
import com.reno.philipshue.model.isPhillipsHueBridge
import com.reno.philipshue.network.UPnPService
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

class UPnPDiscoveryManager(
    private val context: Context,
    private val timeOut: Long,
    private val customQuery: String,
    private val internetAddress: String,
    private val port: Int
) : IDiscoveryManager {
    private val devices = hashSetOf<UPnPDevice>()
    private var threadCount = 0

    override suspend fun getBridges(): List<Bridge> {
        val uPnPDeviceSet = discoverDevices().toList()
        val ipAddress = uPnPDeviceSet.filter { it.isPhillipsHueBridge() }[0].location
        val uPnPService = Injector.injectRetrofitService(ipAddress, UPnPService::class.java)
        val bridgeConfig = uPnPService.getBridgeConfig().await()

        return arrayListOf(
            bridgeConfig.convertToBridge(ipAddress)
        )
    }

    suspend fun discoverDevices(): HashSet<UPnPDevice> {
        val wifiManager: WifiManager? =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        return runBlocking(Dispatchers.IO) {
            try {
                withTimeout(timeOut) {
                    wifiManager?.let {
                        val multiCastLock = it.createMulticastLock(internetAddress)
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
                                    getData(device.location, device)
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
                }
            } catch (exception: Exception) {
                throw exception
            }
            devices
        }
    }

    fun discoverDevices(
        discoverListener: IDiscoveryManager.DiscoverListener
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            discoverListener.onStart()
            val wifiManager: WifiManager? =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            CoroutineScope(Dispatchers.IO).async {
                try {
                    withTimeout(timeOut) {
                        wifiManager?.let {
                            val multiCastLock = it.createMulticastLock(internetAddress)
                            multiCastLock.acquire()
                            var socket: DatagramSocket? = null

                            try {
                                val group =
                                    InetAddress.getByName(internetAddress)
                                val port: Int = port
                                val query: String = customQuery

                                socket = DatagramSocket(null)
                                socket.reuseAddress = true
                                socket.broadcast = true
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
                                        getData(device.location, device)
                                    }
                                    curTime = System.currentTimeMillis()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                launch(Dispatchers.Main) {
                                    discoverListener.onError(e)
                                }
                            } finally {
                                socket?.close()
                            }
                            multiCastLock.release()
                        }
                    }
                } catch (exception: Exception) {
                    launch(Dispatchers.Main) {
                        discoverListener.onError(exception)
                    }
                } finally {
                    launch(Dispatchers.Main) {
                        discoverListener.onFinish(devices)
                    }
                }
            }.await()
        }
    }

    private fun getData(
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
        fun build() = UPnPDiscoveryManager(
            context,
            timeOut,
            query,
            address,
            port
        )
    }
}