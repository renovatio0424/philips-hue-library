package com.reno.philipshue.network

import retrofit2.http.POST

interface CallbackService {

    @POST("")
    fun getAuthToken(id:String):String
}