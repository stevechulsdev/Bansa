package com.stevechulsdev.bansa.etc

import android.content.Context
import java.lang.Exception

object Utils {

    private fun setLocalUserDataString(context: Context, key: String, value: String) {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString(key, value).apply()
    }

    private fun setLocalUserDataBoolean(context: Context, key: String, value: Boolean) {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(key, value).apply()
    }

    fun getLocalUserDataString(context: Context, key: String): String? {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        try {
            return preference.getString(key, "")
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getLocalUserDataBoolean(context: Context, key: String): Boolean {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        try {
            return preference.getBoolean(key, false)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun setLocalUserDataUid(context: Context, value: String) {
        setLocalUserDataString(context, Constants.LOCAL_DATA_KEY_USER_UID, value)
    }

    fun setLocalUserDataNickName(context: Context, value: String) {
        setLocalUserDataString(context, Constants.LOCAL_DATA_KEY_USER_NICKNAME, value)
    }

    fun setLocalUserDataLoginType(context: Context, loginType: Constants.LoginType) {
        setLocalUserDataString(context, Constants.LOCAL_DATA_KEY_USER_LOGIN_TYPE, loginType.name)
    }

    fun setLocalUserDataIsLogin(context: Context, value: Boolean) {
        setLocalUserDataBoolean(context, Constants.LOCAL_DATA_KEY_USER_IS_LOGIN, value)
    }
}