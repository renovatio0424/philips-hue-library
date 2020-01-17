package com.reno.philipshue.bridge.remote.repository.token.data_source

import android.content.SharedPreferences
import kotlinx.coroutines.Deferred

class LocalDataSource(private val preferences: SharedPreferences): ILocalDataSource {
    override fun getTokenAsync(): Deferred<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface ILocalDataSource: IDataSource