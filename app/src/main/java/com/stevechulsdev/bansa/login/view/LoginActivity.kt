package com.stevechulsdev.bansa.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.firebase.ScSnsGoogle
import com.stevechulsdev.bansa.login.viewmodel.LoginViewModel
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Constants.REQUEST_CODE_GOOGLE_LOGIN -> {
                data?.let {
                    ScSnsGoogle.getUser(this, data, object : ScSnsGoogle.GetUserInterface {
                        override fun success(user: FirebaseUser) {
                            ScLog.e(Constants.IS_DEBUG, "ScSnsGoogle.getUser success")

                            setUserData(user.uid)
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

    private fun setUserData(googleUid: String) {
        DBManager().readUserData(googleUid, object : DBManager.OnReadStatusListener {
            override fun onSuccess(uid: String, nickname: String) {
                if(googleUid == uid) {
                    Toast.makeText(this@LoginActivity, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show()

                    Utils.setLocalUserDataUid(this@LoginActivity, googleUid)
                    Utils.setLocalUserDataNickName(this@LoginActivity, nickname)

                    LocalPreference.userUid = googleUid
                    LocalPreference.userNickName = nickname

                    setResult(Constants.RESULT_CODE_BACK_LOGIN)
                    finish()
                    overridePendingTransition(0,0)

                    // hide progressbar
                    ScDisplayUtils.hideProgressBar()
                    return
                }
                else {
                    // timestamp is nickname
                    val nickname = System.currentTimeMillis().toString()

                    DBManager().insertUserData(googleUid, nickname, object : DBManager.OnInsertStatusListener {
                        override fun onSuccess() {
                            Utils.setLocalUserDataUid(this@LoginActivity, googleUid)
                            Utils.setLocalUserDataNickName(this@LoginActivity, nickname)

                            LocalPreference.userUid = googleUid
                            LocalPreference.userNickName = nickname

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
}
