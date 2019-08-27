package com.stevechulsdev.bansa

import android.app.Application
import android.content.Context
import com.kakao.auth.*

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
    }
}