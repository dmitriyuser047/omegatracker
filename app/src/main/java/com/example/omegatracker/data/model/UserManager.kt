package com.example.omegatracker.data.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

const val USER_TOKEN = "token"
const val USER_ID = "id"
const val USER_ICON = "icon"
const val USER_URL = "url"

class UserManager @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private var token: String? = null

    fun setUser(newToken: String, id: String, iconUrl: String, clientUrl: String) {
        token = newToken
        sharedPreferences.edit {
            putString(USER_TOKEN, token)
            putString(USER_ID, id)
            putString(USER_ICON, iconUrl)
            putString(USER_URL, clientUrl)
        }
    }

    fun getIcon(): String? {
        return sharedPreferences.getString(USER_ICON, null)
    }

    fun getToken(): String? {
        return sharedPreferences.getString(USER_TOKEN, null)
    }

    fun getUser(): String? {
        return sharedPreferences.getString(USER_ID, null)
    }

    fun getUserUrl(): String? {
        return sharedPreferences.getString(USER_URL, null)
    }

    fun exit() {
        sharedPreferences.edit {
            clear().apply()
        }
        println(getToken())
    }
}