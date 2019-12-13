package com.reno.philipshue

import com.reno.philipshue.model.Bulb

class BulbFactory : IBulbFactory {

    companion object {

    }
    override fun create(): Bulb {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface IBulbFactory {
    fun create():Bulb
}
