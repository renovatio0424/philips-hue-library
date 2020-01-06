package com.reno.philipshue.bridge.remote.repository.token

import com.reno.philipshue.bridge.remote.repository.token.data_source.ILocalDataSource
import com.reno.philipshue.bridge.remote.repository.token.data_source.IRemoteDataSource
import kotlinx.coroutines.Deferred

class TokenRepository(
    private val localDataSource: ILocalDataSource,
    private val remoteDataSource: IRemoteDataSource
):
    ITokenRepository {
    override suspend fun getTokenAsync(): Deferred<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface ITokenRepository {
    suspend fun getTokenAsync():Deferred<String>
}