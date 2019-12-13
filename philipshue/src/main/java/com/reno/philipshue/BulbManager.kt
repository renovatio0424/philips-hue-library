package com.reno.philipshue

import com.reno.philipshue.network.BulbService
import com.reno.philipshue.model.UserInfo
import io.reactivex.Observable

class BulbManager : IBulbManager {
    override fun getUserInfo(): Observable<UserInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBulbService(): Observable<BulbService> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

interface IBulbManager {
    fun getUserInfo(): Observable<UserInfo>
    fun getBulbService(): Observable<BulbService>
}
