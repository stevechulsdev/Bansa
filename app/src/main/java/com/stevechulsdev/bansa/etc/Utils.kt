package com.stevechulsdev.bansa.etc

import android.content.Context
import java.lang.Exception

object Utils {

    private fun setLocalUserData(context: Context, key: String, value: String) {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString(key, value).apply()
    }

    fun getLocalUserData(context: Context, key: String): String? {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        try {
            return preference.getString(key, "")
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun setLocalUserDataUid(context: Context, value: String) {
        setLocalUserData(context, Constants.LOCAL_DATA_KEY_USER_UID, value)
    }

    fun setLocalUserDataNickName(context: Context, value: String) {
        setLocalUserData(context, Constants.LOCAL_DATA_KEY_USER_NICKNAME, value)
    }
}