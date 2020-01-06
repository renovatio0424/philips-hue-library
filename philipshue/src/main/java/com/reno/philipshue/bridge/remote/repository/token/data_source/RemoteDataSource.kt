package com.reno.philipshue.bridge.remote.repository.token.data_source

import com.reno.philipshue.network.RemoteHueAuthService
import kotlinx.coroutines.Deferred

class RemoteDataSource(private val remoteHueAuthService: RemoteHueAuthService) : IRemoteDataSource {
    override fun getTokenAsync(): Deferred<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface IRemoteDataSource : IDataSource