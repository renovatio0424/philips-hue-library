package com.reno.philipshue.di

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.reno.philipshue.bridge.*
import com.reno.philipshue.network.BridgeService
import com.reno.philipshue.network.NUPnPService
import com.reno.philipshue.network.UPnPService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhilipsHueContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {

        startKoin {
            printLogger()
            modules(hueModules)
            androidContext(context as Application)
        }
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}

val networkModule = module {
    //bridgeService
    factory<BridgeService> { (baseUrl: String) ->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BridgeService::class.java)
    }

    factory<NUPnPService> { (baseUrl: String) ->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NUPnPService::class.java)
    }

    factory<UPnPService> { (baseUrl:String)->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UPnPService::class.java)
    }
}

val managerModule = module {
    factory<IUPnPDiscoveryManager> {
        UPnPDiscoveryManager(get())
    }

    factory<INUPnPDiscoveryManager> {
        NUPnPDiscoveryManager()
    }

    factory<ISocketDiscoveryManager> {
        SocketDiscoveryManager
            .Builder(androidContext())
            .build()
    }
}

val hueModules = listOf(
    networkModule,
    managerModule
)

