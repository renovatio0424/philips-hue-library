package com.reno.philipshue.model

import com.reno.philipshue.network.BulbService

data class Bulb(
    private val isOn:Boolean
) {

}

interface IBulb {
    fun turnOn()
    fun turnOff()
}