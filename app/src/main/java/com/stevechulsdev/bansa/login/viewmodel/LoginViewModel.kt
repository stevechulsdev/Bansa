package com.stevechulsdev.bansa.login.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog

class LoginViewModel: ViewModel() {

    private val callOnClickGoogleLogin = MutableLiveData<Void>()
    val liveDataOnClickGoogleLogin: LiveData<Void>
        get() = callOnClickGoogleLogin

    private val callOnClickKakaoLogin = MutableLiveData<Void>()
    val liveDataOnClickKakaoLogin: LiveData<Void>
        get() = callOnClickKakaoLogin

    private val callOnClickGoBack = MutableLiveData<Void>()
    val liveDataOnClickGoBack: LiveData<Void>
        get() = callOnClickGoBack

    private val callMoveMainActivity = MutableLiveData<Void>()
    val liveDataMoveMainActivity: LiveData<Void>
        get() = callMoveMainActivity

    private val callGetGoogleUser = MutableLiveData<Intent>()
    val liveDataGetGoogleUser: LiveData<Intent>
        get() = callGetGoogleUser

    private val callSetLocalData = MutableLiveData<Boolean>()
    val liveDataSetLocalData: LiveData<Boolean>
        get() = callSetLocalData

    private val callGetUserDataSuccess = MutableLiveData<MeV2Response>()
    val liveDataGetUserDataSuccess: LiveData<MeV2Response>
        get() = callGetUserDataSuccess

    fun onCreate() {
        KakaoManager.initKakao()
    }

    fun onDestroy() {
        KakaoManager.removeCallback()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Kakao Login Process 진행 중,
//        if(LocalPreference.loginType == Constants.LoginType.KAKAO.name) {
//            Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)
//        }
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return

        when(requestCode) {
            Constants.REQUEST_CODE_GOOGLE_LOGIN -> {
                data?.let {
                    callGetGoogleUser.postValue(data)
                }
            }
        }
    }

    fun onClickGoogleLogin() {
        callOnClickGoogleLogin.postValue(null)
    }

    fun onClickKakaoLogin() {
        callOnClickKakaoLogin.postValue(null)
//        callOnClickKakaoLogin.postValue(KakaoManager.setCallback())
    }

    fun onClickGoBack() {
        callOnClickGoBack.postValue(null)
    }

    fun setUserData(uid: String, loginType: Constants.LoginType) {
        DBManager().checkUserData(uid, object : DBManager.OnCheckStatusListener {
            override fun onSuccess(isMember: Boolean, uid: String, nickname: String) {
                if(isMember) {
                    LocalPreference.userUid = uid
                    LocalPreference.userNickName = nickname
                    LocalPreference.loginType = loginType.name
                    LocalPreference.isLogin = true

                    callSetLocalData.postValue(false)
                }
                else {
                    // timestamp is nickname
                    val nickname = System.currentTimeMillis().toString().substring(7)

                    DBManager().insertUserData(uid, nickname, loginType.name, object : DBManager.OnInsertStatusListener {
                        override fun onSuccess() {
                            LocalPreference.userUid = uid
                            LocalPreference.userNickName = nickname
                            LocalPreference.loginType = loginType.name
                            LocalPreference.isLogin = true

                            callSetLocalData.postValue(true)
                        }

                        override fun onFail(e: Exception) {
                            ScDisplayUtils.hideProgressBar()
                        }
                    })
                }
            }
            override fun onFail(e: Exception) {
                // hide progressbar
                ScDisplayUtils.hideProgressBar()
            }
        })
    }

    fun kakaoLogin() {
        // Kakao Login
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                result?.let {
                    callGetUserDataSuccess.postValue(result)
                }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                errorResult?.let {
                    ScLog.e(true, "UserManagement.getInstance().me onSessionClosed error : $errorResult")
                }
                ScDisplayUtils.hideProgressBar()
            }

            override fun onFailure(errorResult: ErrorResult?) {
                errorResult?.let {
                    ScLog.e(true, "UserManagement.getInstance().me error : $errorResult")
                }
                ScDisplayUtils.hideProgressBar()
            }
        })
    }
}