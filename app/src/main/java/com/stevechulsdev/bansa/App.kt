package com.stevechulsdev.bansa

import android.app.Application
import android.content.Context
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils

class App: Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun globalContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        if(!Utils.getLocalUserDataString(globalContext(), Constants.LOCAL_DATA_KEY_USER_UID).isNullOrEmpty()) {
            LocalPreference.userUid = Utils.getLocalUserDataString(globalContext(), Constants.LOCAL_DATA_KEY_USER_UID)!!
        }

        if(!Utils.getLocalUserDataString(globalContext(), Constants.LOCAL_DATA_KEY_USER_NICKNAME).isNullOrEmpty()) {
            LocalPreference.userNickName = Utils.getLocalUserDataString(globalContext(), Constants.LOCAL_DATA_KEY_USER_NICKNAME)!!
        }

        if(!Utils.getLocalUserDataString(globalContext(), Constants.LOCAL_DATA_KEY_USER_LOGIN_TYPE).isNullOrEmpty()) {
            LocalPreference.loginType = Utils.getLocalUserDataString(globalContext(), Constants.LOCAL_DATA_KEY_USER_LOGIN_TYPE)!!
        }

        if(Utils.getLocalUserDataBoolean(this, Constants.LOCAL_DATA_KEY_USER_IS_LOGIN)) {
            LocalPreference.isLogin = true
        }
    }
}