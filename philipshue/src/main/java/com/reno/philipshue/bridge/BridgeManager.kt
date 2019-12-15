package com.reno.philipshue.bridge

import android.content.Context
import com.reno.philipshue.injector.Injector
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.BridgeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

const val BRIDGE_BASE_URL: String = ""

class BridgeManager(private val context: Context) : IBridgeManager {
    private val bridgeService: BridgeService = Injector.injectBridgeService(BRIDGE_BASE_URL)
    private val uPnPManager: IDiscoveryManager = Injector.injectUPnPManager(context)

    override fun connectBridge(
        onSuccess: (Bridge) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            var result: Bridge? = null

            CoroutineScope(Dispatchers.IO).async {

//                uPnPManager.discoverDevices(context)
            }.await()

            if (result != null)
                onSuccess(result!!)
            else
                onError(Exception("Bridge is null"))

            onComplete()
        }
    }

}

interface IBridgeManager {
    fun connectBridge(
        onSuccess: (Bridge) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    )
}
