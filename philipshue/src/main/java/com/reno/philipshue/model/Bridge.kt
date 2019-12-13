package com.reno.philipshue.model

data class Bridge(
    private val id: String,
    private val ipAddress: String,
    private val macAddress: String?,
    private val name: String?
)
