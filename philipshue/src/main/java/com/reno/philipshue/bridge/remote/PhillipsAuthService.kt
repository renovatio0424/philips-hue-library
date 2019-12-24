package com.reno.philipshue.bridge.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface PhillipsAuthService {
    @GET("oauth2/auth?clientid={clientId}&appid={appId}&devicename={deviceName}&state={state}&response_type=code")
    fun getToken(
        @Path("clientId") clientId:String,
        @Path("appId") appId:String,
        @Path("deviceName") deviceName:String,
        @Path("state") state:String
    )


    @GET("oauth2/auth?clientid={clientId}&appid={appId}&state={state}&response_type=code")
    fun getToken(
        @Path("clientId") clientId:String,
        @Path("appId") appId:String,
        @Path("state") state:String
    )
}