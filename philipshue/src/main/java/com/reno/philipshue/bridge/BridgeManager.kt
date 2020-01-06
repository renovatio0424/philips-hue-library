package com.reno.philipshue.bridge

import com.reno.philipshue.bridge.local.ILocalBridgeManager
import com.reno.philipshue.bridge.remote.IRemoteBridgeManager
import com.reno.philipshue.model.Bridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

/**
 *
 * Architecture about BridgeManager
 * @author Reno.kim
 *
 * -------------------------------------------------------------------------------
 *                               BridgeManager
 * -------------------------------------------------------------------------------
 *               LocalBridgeManager               |        RemoteBridgeManager
 * -------------------------------------------------------------------------------
 *  UPnPDiscoveryManager  |                       |         TokenRepository
 *  ----------------------| NUPnPDiscoveryManager |
 * SocketDiscoveryManager |                       |
 *
 * */

class BridgeManager : IBridgeManager {
    private val localBridgeManager: ILocalBridgeManager by inject(ILocalBridgeManager::class.java)
    private val remoteBridgeManager: IRemoteBridgeManager by inject(IRemoteBridgeManager::class.java)

    override fun connectBridge(
        onSuccess: (List<Bridge>) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    val bridges = localBridgeManager.getBridgeAsync()
                    onSuccess(bridges)
                } catch (e: Exception) {
                    onError(e)
                } finally {
                    onComplete.invoke()
                }
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
