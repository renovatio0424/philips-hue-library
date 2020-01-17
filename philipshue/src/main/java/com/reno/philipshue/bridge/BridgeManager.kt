package com.reno.philipshue.bridge

import com.reno.philipshue.bridge.local.ILocalBridgeDiscovery
import com.reno.philipshue.bridge.remote.IRemoteBridgeDiscovery
import com.reno.philipshue.model.Bridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.CoroutineContext

/**
 *
 * Architecture about BridgeManager
 * @author Reno.kim
 *
 * -------------------------------------------------------------------------------
 *                               BridgeManager
 * -------------------------------------------------------------------------------
 *               LocalBridgeDiscovery      |        RemoteBridgeDiscovery
 * -------------------------------------------------------------------------------
 *  UPnPDiscovery  |                       |         TokenRepository
 *  ---------------| NUPnPDiscoveryManager |
 * SocketDiscovery |                       |
 *
 * */

class BridgeManager : IBridgeManager {
    private val localBridgeDiscovery: ILocalBridgeDiscovery by inject(ILocalBridgeDiscovery::class.java)
    private val remoteBridgeDiscovery: IRemoteBridgeDiscovery by inject(IRemoteBridgeDiscovery::class.java)

    override fun connectBridge(
        onSuccess: (List<Bridge>) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                try {
                    val bridges = localBridgeDiscovery.getBridgeAsync()
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
