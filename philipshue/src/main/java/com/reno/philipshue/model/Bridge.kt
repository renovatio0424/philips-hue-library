package com.reno.philipshue.model

import com.google.gson.annotations.SerializedName

data class Bridge(
    @SerializedName("id")
    private val id: String,
    @SerializedName("internalipaddress")
    private val internalIpAddress: String,
    @SerializedName("macaddress")
    private val macAddress: String?,
    @SerializedName("name")
    private val name: String?
)
