package com.stevechulsdev.bansa.kakao

import com.kakao.auth.*
import com.kakao.util.exception.KakaoException
import com.stevechulsdev.bansa.App

object KakaoManager {

    private var callback: ISessionCallback? = null

    fun initKakao() {
        try {
            // kakao login init
            KakaoSDK.init(KakaoSDKAdapter)
        }
        catch (e: KakaoSDK.AlreadyInitializedException) {
            return
        }
    }

    fun setCallback(kakaoSessionListener: KakaoSessionListener): Session {
        // 이게 로그인 되었는지 체크를 함
        val session = Session.getCurrentSession()

        callback = object : ISessionCallback {
            override fun onSessionOpened() {
                kakaoSessionListener.onSessionOpened()
            }

            override fun onSessionOpenFailed(exception: KakaoException?) {
                kakaoSessionListener.onSessionOpenFailed(exception)
            }
        }

        session.addCallback(callback)
//        session.checkAndImplicitOpen()

        return session
    }

    fun removeCallback() {
        callback?.let {
            Session.getCurrentSession().removeCallback(callback)
        }

        callback = null
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

    interface KakaoSessionListener {
        fun onSessionOpened()
        fun onSessionOpenFailed(exception: KakaoException?)
    }
}