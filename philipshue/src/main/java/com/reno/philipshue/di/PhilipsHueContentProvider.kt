package com.reno.philipshue.di

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.reno.philipshue.bridge.local.*
import com.reno.philipshue.bridge.remote.IRemoteBridgeDiscovery
import com.reno.philipshue.bridge.remote.RemoteBridgeDiscovery
import com.reno.philipshue.bridge.remote.repository.token.ITokenRepository
import com.reno.philipshue.bridge.remote.repository.token.TokenRepository
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
    factory<NUPnPService> { (baseUrl: String) ->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NUPnPService::class.java)
    }

    factory<UPnPService> { (baseUrl: String) ->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UPnPService::class.java)
    }

}

val managerModule = module {
    factory<IUPnPDiscoveryManager> {
        UPnPDiscovery(get())
    }

    factory<INUPnPDiscoveryManager> {
        NUPnPDiscovery()
    }

    factory<ISocketDiscoveryManager> {
        SocketDiscoveryManager
            .Builder(androidContext())
            .build()
    }

    factory<ILocalBridgeDiscovery> { LocalBridgeDiscovery(get(), get()) }

    factory<IRemoteBridgeDiscovery> { RemoteBridgeDiscovery(get()) }

}

val repositoryModule = module {
    single<ITokenRepository> {
        TokenRepository(get(), get())
    }

    single<SharedPreferences> {
        PreferenceManager.getDefaultSharedPreferences(androidContext())
    }

    factory {
        Gson()
    }

}

val hueModules = listOf(
    networkModule,
    managerModule,
    repositoryModule
)

