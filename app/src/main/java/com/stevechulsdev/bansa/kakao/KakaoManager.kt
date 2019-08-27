package com.stevechulsdev.bansa.kakao

import com.kakao.auth.*
import com.stevechulsdev.bansa.App

class KakaoManager {

    fun initKakao() {
        try {
            // kakao login init
            KakaoSDK.init(KakaoSDKAdapter)
        }
        catch (e: KakaoSDK.AlreadyInitializedException) {
            return
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
            return IApplicationConfig { App.globalContext() }
        }
    }
}