package com.reno.philipshue.bridge.remote.repository.token

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.reno.philipshue.model.Token

private const val PREF_TOKEN = "token"
private const val PREF_KEY = "key"

class TokenRepository(
    private val preferences: SharedPreferences,
    private val gson: Gson
) :
    ITokenRepository {

    override fun getToken(): Token? {
        return getValue(PREF_TOKEN, Token::class.java)
    }

    override fun saveToken(token: Token) {
        setValueAsync(PREF_TOKEN, token)
    }

    override fun getKey(): String? {
        return getValue(PREF_KEY, String::class.java)
    }

    override fun saveKey(key: String) {
        setValueAsync(key, String::class.java)
    }

    private fun <T> getValue(key: String, clazz: Class<T>): T? {
        return preferences.getString(key, null)?.let {
            gson.fromJson(it, clazz)
        }
    }

    private fun <T> setValueAsync(key: String, value: T) {
        preferences.edit {
            putString(key, gson.toJson(value))
        }
    }
}

interface ITokenRepository {
    fun getToken(): Token?
    fun saveToken(token: Token)
    fun getKey(): String?
    fun saveKey(key: String)
}