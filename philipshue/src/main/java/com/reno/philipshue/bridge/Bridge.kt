package com.reno.philipshue.bridge

import com.google.gson.annotations.SerializedName

data class Bridge(
    @SerializedName("id")
    val id: String,
    @SerializedName("internalipaddress")
    val internalIpAddress: String,
    @SerializedName("macaddress")
    val macAddress: String?,
    @SerializedName("name")
    val name: String?
)
