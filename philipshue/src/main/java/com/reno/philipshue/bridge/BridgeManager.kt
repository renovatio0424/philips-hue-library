package com.reno.philipshue.bridge

import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.BridgeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

const val BRIDGE_BASE_URL: String = ""

class BridgeManager : IBridgeManager {
    private val bridgeService: BridgeService by inject(BridgeService::class.java) {
        parametersOf(BRIDGE_BASE_URL)
    }
    private val nUPnPDiscoveryManager: INUPnPDiscoveryManager by inject(INUPnPDiscoveryManager::class.java)
    private val uPnPManager: IUPnPDiscoveryManager by inject(IUPnPDiscoveryManager::class.java)

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
