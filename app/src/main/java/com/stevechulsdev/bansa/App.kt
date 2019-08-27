package com.stevechulsdev.bansa

import android.app.Application
import android.content.Context
import com.kakao.auth.*
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

    // kakao login
    object KakaoSDKAdapter: KakaoAdapter() {
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_TALK)
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

                override fun isSecureMode(): Boolean {
                    return false
                }

                override fun getApprovalType(): ApprovalType? {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isSaveFormData(): Boolean {
                    return true
                }
            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return IApplicationConfig { globalContext() }
        }
    }

    override fun onCreate() {
        super.onCreate()

        // kakao login init
        KakaoSDK.init(KakaoSDKAdapter)

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