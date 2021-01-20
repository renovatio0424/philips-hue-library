package com.reno.philipshue.bridgecontrol

import androidx.annotation.IntRange
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface BridgeControlApi {

    /**
     * Error Response
    *[
    *   {
    *       "error": {
    *           "type": 101,
    *           "address": "",
    *           "description": "link button not pressed"
    *       }
    *   }
    *]
     * */

    /**
     * Success Response
     *[
     *  {
     *      "success": {
     *          "username": "dhnitVd9sHhDgUt9rsLQGKOq3izCDAXQDu3CdH1L"
     *      }
     *   }
     *]
     * */
    @POST("/api")
    suspend fun getToken(@Body requestBody: BridgeTokenRequestBody): JsonArray

    @GET("/api/{token}/lights")
    suspend fun getLightList(@Path("token") token: String): Map<String, Light>

    @GET("/api/{token}/lights/{lightId}")
    suspend fun getLight(@Path("token") token: String, @Path("lightId") lightId: Int): Light

    @PUT("/api/{token}/lights/{lightId}/state")
    suspend fun turnOn(
        @Path("token") token: String,
        @Path("lightId") lightId: Int,
        @Body bridgeLightRequestBody: BridgeLightRequestBody
    )

    @PUT("/api/{token}/lights/{lightId}/state")
    suspend fun changeColor(
        @Path("token") token: String,
        @Path("lightId") lightId: Int,
        @Body bridgeLightRequestBody: BridgeLightRequestBody
    )
}

data class BridgeTokenRequestBody(
    @SerializedName("devicetype")
    private val deviceType: String
)

data class BridgeLightRequestBody(
    @SerializedName("on")
    private val on: Boolean,

    @SerializedName("sat")
    @IntRange(from = 0, to = 254)
    private val saturation: Int? = null,

    @SerializedName("bri")
    @IntRange(from = 0, to = 254)
    private val brightness: Int? = null,

    //Maximum value = 65535 = 182.04 * 360
    @SerializedName("hue")
    @IntRange(from = 0, to = 65535)
    private val hue: Int? = null
)