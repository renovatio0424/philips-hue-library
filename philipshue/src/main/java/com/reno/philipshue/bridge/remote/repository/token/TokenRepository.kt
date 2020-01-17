package com.reno.philipshue.bridge.remote.repository.token

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.reno.philipshue.model.Token

private const val PREF_TOKEN = "token"

class TokenRepository(
    private val preferences: SharedPreferences,
    private val gson: Gson
) :
    ITokenRepository {

    override fun getToken(): Token? {
        return preferences.getString(PREF_TOKEN, null)
            ?.let {
                gson.fromJson(it, Token::class.java)
            }
    }

    override fun saveToken(token: Token) {
        preferences.edit {
            putString(PREF_TOKEN, gson.toJson(token))
        }
    }
}

interface ITokenRepository {
    fun getToken(): Token?
    fun saveToken(token: Token)
}