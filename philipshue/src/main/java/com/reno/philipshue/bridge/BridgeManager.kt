package com.reno.philipshue.bridge

import android.content.Context
import com.reno.philipshue.injector.Injector
import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.BridgeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val BRIDGE_BASE_URL: String = ""

class BridgeManager(private val context: Context) : IBridgeManager {
    private val bridgeService: BridgeService = Injector.injectBridgeService(BRIDGE_BASE_URL)
    private val nUPnPDiscoveryManager: IDiscoveryManager = Injector.injectNUPnPManager()
    private val uPnPManager: IDiscoveryManager = Injector.injectUPnPManager(context)

    override fun connectBridge(
        onSuccess: (List<Bridge>) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    val bridges = uPnPManager.getBridges()
                    onSuccess(bridges)
                } catch (e: Exception) {
                    onError(e)
                } finally {
                    onComplete.invoke()
                }
                //                uPnPManager.discoverDevices(context)
            }
        }
    }

}

interface IBridgeManager {
    fun connectBridge(
        onSuccess: (List<Bridge>) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    )
}
