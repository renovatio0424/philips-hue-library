package com.reno.philipshue.bridgecontrol

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import java.lang.IllegalStateException
import java.util.*

interface IBridgeController {
    suspend fun getToken(userName: String): String
    suspend fun getToken(): String
    suspend fun getLights(token: String): List<Light>
    suspend fun getLight(token: String, lightIdx: Int): Light
    suspend fun turnOn(token: String, lightIdx: Int, isOn: Boolean)
    suspend fun changeColor(
        token: String,
        lightIdx: Int,
        @ColorInt
        color: Int
    )

    suspend fun changeRGBColor(
        token: String,
        lightIdx: Int,
        @IntRange(from = 0, to = 254)
        red: Int,
        @IntRange(from = 0, to = 254)
        green: Int,
        @IntRange(from = 0, to = 254)
        blue: Int,
    )

    suspend fun changeHSVColor(
        token: String,
        lightIdx: Int,
        @IntRange(from = 0, to = 254)
        brightness: Int,
        @IntRange(from = 0, to = 254)
        saturation: Int,
        @IntRange(from = 0, to = 65535)
        hue: Int
    )
}

class BridgeController(bridgeIp: String) : IBridgeController {

    private val bridgeControlApi: BridgeControlApi by inject(BridgeControlApi::class.java) {
        parametersOf(
            bridgeIp
        )
    }

    @Throws(UnClickBridgeLinkButton::class, UnknownBridgeException::class)
    override suspend fun getToken(userName: String): String {
        return withContext(Dispatchers.Default) {
            val response = bridgeControlApi.getToken(BridgeTokenRequestBody(userName))
            val jsonObject = response[0]
            parseResponse(jsonObject)
        }
    }

    @Throws(UnClickBridgeLinkButton::class, UnknownBridgeException::class)
    override suspend fun getToken(): String {
        return withContext(Dispatchers.Default) {
            val response =
                bridgeControlApi.getToken(BridgeTokenRequestBody(UUID.randomUUID().toString()))
            val jsonObject = response[0]
            parseResponse(jsonObject)
        }
    }

    private fun parseResponse(jsonObject: JsonElement): String {
        return when {
            jsonObject.asJsonObject.has("success") -> {
                jsonObject.asJsonObject.get("success").asJsonObject.get("username").asString
            }
            jsonObject.asJsonObject.has("error") -> {
                val type = jsonObject.asJsonObject["error"].asJsonObject["type"].asInt
                if (type == 101) {
                    throw UnClickBridgeLinkButton()
                } else {
                    val description =
                        jsonObject.asJsonObject["error"].asJsonObject["description"].asString
                    throw UnknownBridgeException(type, description)
                }
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override suspend fun getLights(token: String): List<Light> {
        return withContext(Dispatchers.Default) {
            val response = bridgeControlApi.getLightList(token)
            response.map { it.value.apply { id = it.key.toInt() } }
        }
    }

    override suspend fun getLight(token: String, lightIdx: Int): Light {
        return bridgeControlApi.getLight(token, lightIdx)
    }

    override suspend fun turnOn(token: String, lightIdx: Int, isOn: Boolean) {
        return bridgeControlApi.turnOn(token, lightIdx, BridgeLightRequestBody(on = isOn))
    }

    @SuppressLint("Range")
    override suspend fun changeColor(token: String, lightIdx: Int, color: Int) {
        return changeRGBColor(
            token,
            lightIdx,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    override suspend fun changeRGBColor(
        token: String,
        lightIdx: Int,
        red: Int,
        green: Int,
        blue: Int
    ) {
        val hsv = FloatArray(3)
        Color.colorToHSV(Color.rgb(red, green, blue), hsv)
        val brightness = (hsv[2] * 255).toInt()
        val saturation = (hsv[1] * 255).toInt()
        val hue = ((hsv[0] * 65535) / 360).toInt()
        Log.d("color", "brightness: $brightness, saturation: $saturation, hue: $hue")
        return changeHSVColor(
            token,
            lightIdx,
            brightness,
            saturation,
            hue
        )
    }

    override suspend fun changeHSVColor(
        token: String,
        lightIdx: Int,
        brightness: Int,
        saturation: Int,
        hue: Int
    ) {
        return bridgeControlApi.changeColor(
            token, lightIdx, BridgeLightRequestBody(
                on = true,
                brightness = brightness,
                saturation = saturation,
                hue = hue
            )
        )
    }
}