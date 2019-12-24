package com.reno.philipshue.bridge.remote

import com.reno.philipshue.model.Bridge

interface RemoteBridgeManager {
    suspend fun getBridge(): Bridge
}