package com.reno.philipshue.bridge

import android.content.Context
import android.net.wifi.WifiManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.reno.philipshue.model.UPnPDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

const val DEFAULT_TIME_OUT = 5000
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
    private val timeOut: Int,
    private val customQuery: String,
    private val internetAddress: String,
    private val port: Int
) : IUPnpDiscoveryManager {
    private val devices = hashSetOf<UPnPDevice>()
    private var threadCount = 0

    override fun discoverDevices(
        uPnPListener: IUPnpDiscoveryManager.UPnPListener
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            uPnPListener.onStart()
            val wifiManager: WifiManager? =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            CoroutineScope(Dispatchers.IO).async {
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
                                getData(device.location, device, uPnPListener)
                            }
                            curTime = System.currentTimeMillis()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        CoroutineScope(Dispatchers.Main).launch {
                            uPnPListener.onError(e)
                        }
                    } finally {
                        socket?.close()
                    }
                    multiCastLock.release()
                }
            }.await()
        }
    }

    private fun getData(
        url: String,
        device: UPnPDevice,
        uPnPListener: IUPnpDiscoveryManager.UPnPListener
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String?> { response ->
                device.update(response)
                CoroutineScope(Dispatchers.Main).launch {
                    uPnPListener.onFoundNewDevice(device)
                }

                devices.add(device)
                threadCount--
                if (threadCount == 0)
                    CoroutineScope(Dispatchers.Main).launch {
                        uPnPListener.onFinish(devices)
                    }

            }, Response.ErrorListener {
                threadCount--
                CoroutineScope(Dispatchers.Main).launch {
                    uPnPListener.onError(it)
                }
            })
        Volley.newRequestQueue(context).add(stringRequest)
    }

    data class Builder(
        var context: Context,
        var timeOut: Int = DEFAULT_TIME_OUT,
        var query: String = DEFAULT_QUERY,
        var address: String = DEFAULT_ADDRESS,
        var port: Int = DEFAULT_PORT
    ) {
        fun timeOut(timeOut: Int?) = apply { this.timeOut }
        fun query(query: String?) = apply { this.query }
        fun address(address: String?) = apply { this.address }
        fun port(port: Int?) = apply { this.port }
        fun build() = UPnPDiscoveryManager(context, timeOut, query, address, port)
    }
}

interface IUPnpDiscoveryManager {
    fun discoverDevices(uPnPListener: UPnPListener)

    interface UPnPListener {
        fun onStart()
        fun onFoundNewDevice(device: UPnPDevice)
        fun onFinish(devices: HashSet<UPnPDevice>)
        fun onError(exception: Exception)
    }
}