package com.stevechulsdev.bansa.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.firebase.ScSnsGoogle
import com.stevechulsdev.bansa.login.viewmodel.LoginViewModel
import com.stevechulsdev.bansa.main.view.MainActivity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    var loginViewModel = LoginViewModel()

    private var callback: SessionCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        KakaoSDK.init(KakaoSDK.getAdapter())

//        FirebaseApp.initializeApp(this)
//        val dataBinding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login) as ActivityLoginBinding

//        loginViewModel = LoginViewModel()
//        dataBinding.vm = loginViewModel

//        loginViewModel.liveDataOnClickGoBack.observe(this, Observer {
//            finish()
//            overridePendingTransition(0,0)
//        })

//        loginViewModel.onCreate()

        tv_go_back.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        bt_google_login.setOnClickListener {
            ScSnsGoogle.initLogin(this, Constants.GOOGLE_API_KEY)

            if(!ScSnsGoogle.isLogin()) {
                // show progressbar
                ScDisplayUtils.showProgressBar(this)
                ScSnsGoogle.login(this, Constants.REQUEST_CODE_GOOGLE_LOGIN)
            }
            else {
                Toast.makeText(this, "이미 로그인 되어 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        bt_custom_kakao_login.setOnClickListener {
            ScDisplayUtils.showProgressBar(this)

            // 이게 로그인 되었는지 체크를 함
            val session = Session.getCurrentSession()
            callback = SessionCallback()
            session.addCallback(callback)
            session.open(AuthType.KAKAO_TALK, this)
        }
    }

    override fun onDestroy() {
        callback?.let {
            Session.getCurrentSession().removeCallback(callback)
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return

        when(requestCode) {
            Constants.REQUEST_CODE_GOOGLE_LOGIN -> {
                data?.let {
                    ScSnsGoogle.getUser(this, data, object : ScSnsGoogle.GetUserInterface {
                        override fun success(user: FirebaseUser) {
                            ScLog.e(Constants.IS_DEBUG, "ScSnsGoogle.getUser success")

                            setUserData(user.uid, Constants.LoginType.GOOGLE)
                        }

                        override fun fail(errorMsg: String) {
                            ScLog.e(Constants.IS_DEBUG, "ScSnsGoogle.getUser error : $errorMsg")

                            // hide progressbar
                            ScDisplayUtils.hideProgressBar()
                        }
                    })
                }
            }
        }
    }

    private fun setUserData(uid: String, loginType: Constants.LoginType) {
        DBManager().checkUserData(uid, object : DBManager.OnCheckStatusListener {
            override fun onSuccess(isMember: Boolean, uid: String, nickname: String) {
                if(isMember) {
                    Toast.makeText(this@LoginActivity, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show()

                    Utils.setLocalUserDataUid(this@LoginActivity, uid)
                    Utils.setLocalUserDataNickName(this@LoginActivity, nickname)
                    Utils.setLocalUserDataLoginType(this@LoginActivity, loginType)

                    LocalPreference.userUid = uid
                    LocalPreference.userNickName = nickname
                    LocalPreference.loginType = loginType.name

                    setResult(Constants.RESULT_CODE_BACK_LOGIN)
                    finish()
                    overridePendingTransition(0,0)

                    // hide progressbar
                    ScDisplayUtils.hideProgressBar()
                    return
                }
                else {
                    // timestamp is nickname
                    val nickname = System.currentTimeMillis().toString().substring(7)

                    DBManager().insertUserData(uid, nickname, loginType.name, object : DBManager.OnInsertStatusListener {
                        override fun onSuccess() {
                            Utils.setLocalUserDataUid(this@LoginActivity, uid)
                            Utils.setLocalUserDataNickName(this@LoginActivity, nickname)
                            Utils.setLocalUserDataLoginType(this@LoginActivity, loginType)

                            LocalPreference.userUid = uid
                            LocalPreference.userNickName = nickname
                            LocalPreference.loginType = loginType.name

                            setResult(Constants.RESULT_CODE_BACK_LOGIN)
                            finish()
                            overridePendingTransition(0,0)

                            // hide progressbar
                            ScDisplayUtils.hideProgressBar()
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
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    result?.let {
                        ScLog.e(true, "id : ${result.id}")
                        ScLog.e(true, "kakaoAccount : ${result.kakaoAccount}")
                        ScLog.e(true, "groupUserToken : ${result.groupUserToken}")
                        ScLog.e(true, "nickname : ${result.nickname}")
                        ScLog.e(true, "id : ${result.profileImagePath}")

                        setUserData(result.id.toString(), Constants.LoginType.KAKAO)

//                        startActivity<MainActivity>()
//                        finish()
//                        overridePendingTransition(0,0)
                    }
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    errorResult?.let {
                        ScLog.e(true, "UserManagement.getInstance().me onSessionClosed error : $errorResult")
                    }

                    startActivity<MainActivity>()
                    finish()
                    overridePendingTransition(0,0)
                }

                override fun onFailure(errorResult: ErrorResult?) {
                    errorResult?.let {
                        ScLog.e(true, "UserManagement.getInstance().me error : $errorResult")
                    }

                    ScDisplayUtils.hideProgressBar()
                }
            })
//            startActivity<MainActivity>()
//            finish()
//            overridePendingTransition(0,0)
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            exception?.let {
                ScLog.e(true, "SessionCallback onSessionOpenFailed error : $exception")
            }

            ScDisplayUtils.hideProgressBar()
        }
    }
}
