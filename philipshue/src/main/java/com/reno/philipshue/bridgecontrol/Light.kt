package com.reno.philipshue.bridgecontrol

import com.google.gson.annotations.SerializedName

data class Light(
    var id: Int = 0,
    @SerializedName("capabilities")
    val capabilities: Capabilities,
    @SerializedName("config")
    val config: Config,
    @SerializedName("manufacturername")
    val manufacturerName: String,
    @SerializedName("modelid")
    val modelId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("productname")
    val productName: String,
    @SerializedName("state")
    val state: State,
    @SerializedName("swupdate")
    val swUpdate: Swupdate,
    @SerializedName("swversion")
    val swVersion: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("uniqueid")
    val uniqueId: String
)

data class Capabilities(
    val certified: Boolean,
    val control: Control,
    val streaming: Streaming
)

data class Config(
    val archetype: String,
    val direction: String,
    val function: String
)

data class State(
    @SerializedName("alert")
    val alert: String,
    @SerializedName("bri")
    val bright: Int,
    @SerializedName("colormode")
    val colorMode: String,
    @SerializedName("ct")
    val ct: Int,
    @SerializedName("effect")
    val effect: String,
    @SerializedName("hue")
    val hue: Int,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("on")
    var on: Boolean,
    @SerializedName("reachable")
    val reachable: Boolean,
    @SerializedName("sat")
    val saturation: Int,
    @SerializedName("xy")
    val xy: List<Float>
)

data class Swupdate(
    @SerializedName("lastinstall")
    val lastInstall: Any,
    val state: String
)

data class Control(
    @SerializedName("colorgamuttype")
    val colorGamutType: String,
    @SerializedName("ct")
    val ct: Ct
)

data class Streaming(
    val proxy: Boolean,
    val renderer: Boolean
)

data class Ct(
    val max: Int,
    val min: Int
)