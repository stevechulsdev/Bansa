package com.stevechulsdev.bansa.login.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog

class LoginViewModel: ViewModel() {

    private var callback: SessionCallback? = null

    private val callOnClickGoogleLogin = MutableLiveData<Void>()
    val liveDataOnClickGoogleLogin: LiveData<Void>
        get() = callOnClickGoogleLogin

    private val callOnClickKakaoLogin = MutableLiveData<Session>()
    val liveDataOnClickKakaoLogin: LiveData<Session>
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

    private val callSetLocalData = MutableLiveData<Void>()
    val liveDataSetLocalData: LiveData<Void>
        get() = callSetLocalData

    fun onCreate() {
        KakaoManager().initKakao()
    }

    fun onDestroy() {
        callback?.let {
            Session.getCurrentSession().removeCallback(callback)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        // 이게 로그인 되었는지 체크를 함
        val session = Session.getCurrentSession()
        callback = SessionCallback()
        session.addCallback(callback)

        callOnClickKakaoLogin.postValue(session)
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

                    callSetLocalData.postValue(null)
                }
                else {
                    // timestamp is nickname
                    val nickname = System.currentTimeMillis().toString().substring(7)

                    DBManager().insertUserData(uid, nickname, loginType.name, object : DBManager.OnInsertStatusListener {
                        override fun onSuccess() {
                            LocalPreference.userUid = uid
                            LocalPreference.userNickName = nickname
                            LocalPreference.loginType = loginType.name

                            callSetLocalData.postValue(null)
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

    inner class SessionCallback: ISessionCallback {
        override fun onSessionOpened() {
            // todo -> 로그인 성공 시, 두 번 호출되는 문제가 있음
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    result?.let {
                        ScLog.e(true, "id : ${result.id}")
                        ScLog.e(true, "kakaoAccount : ${result.kakaoAccount}")
                        ScLog.e(true, "groupUserToken : ${result.groupUserToken}")
                        ScLog.e(true, "nickname : ${result.nickname}")
                        ScLog.e(true, "id : ${result.profileImagePath}")

                        setUserData(result.id.toString(), Constants.LoginType.KAKAO)
                    }
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    errorResult?.let {
                        ScLog.e(true, "UserManagement.getInstance().me onSessionClosed error : $errorResult")
                    }

                    callMoveMainActivity.postValue(null)
                }

                override fun onFailure(errorResult: ErrorResult?) {
                    errorResult?.let {
                        ScLog.e(true, "UserManagement.getInstance().me error : $errorResult")
                    }

                    ScDisplayUtils.hideProgressBar()
                }
            })
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            exception?.let {
                ScLog.e(true, "SessionCallback onSessionOpenFailed error : $exception")
            }

            ScDisplayUtils.hideProgressBar()
        }
    }
}