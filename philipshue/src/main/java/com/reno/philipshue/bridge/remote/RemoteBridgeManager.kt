package com.reno.philipshue.bridge.remote

import com.reno.philipshue.bridge.remote.repository.token.TokenRepository
import com.reno.philipshue.model.Bridge
import kotlinx.coroutines.Deferred

class RemoteBridgeManager(
    private val tokenRepository: TokenRepository
) : IRemoteBridgeManager {
    override suspend fun getBridgeAsync(): Deferred<Bridge> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

interface IRemoteBridgeManager {
    suspend fun getBridgeAsync(): Deferred<Bridge>
}