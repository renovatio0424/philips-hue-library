package com.reno.philipshue.bridge.remote.repository.token.data_source

import kotlinx.coroutines.Deferred

interface IDataSource {
    fun getTokenAsync(): Deferred<String>
}