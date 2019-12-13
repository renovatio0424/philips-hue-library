package com.reno.philipshue.network

import com.reno.philipshue.model.Bulb
import io.reactivex.Observable
import retrofit2.http.POST

interface PhilipsHueService {
    @POST
    fun getUserName(deviceType: String): Observable<Bulb>
}