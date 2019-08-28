package com.stevechulsdev.bansa.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.KakaoSDK
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.stevechulsdev.bansa.BR
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.DBManager
import com.stevechulsdev.bansa.firebase.ScSnsGoogle
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.bansa.login.viewmodel.LoginViewModel
import com.stevechulsdev.bansa.main.view.MainActivity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    private var mContext = this
    private var loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mViewDataBinding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_login)
        mViewDataBinding.setVariable(BR.vm, loginViewModel)

        loginViewModel.apply {
            liveDataOnClickGoogleLogin.observe(mContext, Observer {
                ScSnsGoogle.initLogin(mContext, Constants.GOOGLE_API_KEY)

                if(!ScSnsGoogle.isLogin()) {
                    // show progressbar
                    ScDisplayUtils.showProgressBar(mContext)
                    ScSnsGoogle.login(mContext, Constants.REQUEST_CODE_GOOGLE_LOGIN)
                }
                else {
                    Toast.makeText(mContext, "이미 로그인 되어 있습니다.", Toast.LENGTH_SHORT).show()
                }
            })

            liveDataOnClickKakaoLogin.observe(mContext, Observer {
                ScDisplayUtils.showProgressBar(mContext)

                it.open(AuthType.KAKAO_TALK, mContext)
            })

            liveDataOnClickGoBack.observe(mContext, Observer {
                finish()
                overridePendingTransition(0,0)
            })

            liveDataMoveMainActivity.observe(mContext, Observer {
                startActivity<MainActivity>()
                finish()
                overridePendingTransition(0,0)
            })

            liveDataGetGoogleUser.observe(mContext, Observer {
                ScSnsGoogle.getUser(mContext, it, object : ScSnsGoogle.GetUserInterface {
                    override fun success(user: FirebaseUser) {
                        ScLog.e(Constants.IS_DEBUG, "ScSnsGoogle.getUser success")

                        loginViewModel.setUserData(user.uid, Constants.LoginType.GOOGLE)
                    }

                    override fun fail(errorMsg: String) {
                        ScLog.e(Constants.IS_DEBUG, "ScSnsGoogle.getUser error : $errorMsg")

                        // hide progressbar
                        ScDisplayUtils.hideProgressBar()
                    }
                })
            })

            liveDataSetLocalData.observe(mContext, Observer {
                Toast.makeText(this@LoginActivity, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show()

                Utils.setLocalUserDataUid(this@LoginActivity, LocalPreference.userUid)
                Utils.setLocalUserDataNickName(this@LoginActivity, LocalPreference.userNickName)
                Utils.setLocalUserDataLoginType(this@LoginActivity, LocalPreference.loginType)

                setResult(Constants.RESULT_CODE_BACK_LOGIN)
                finish()
                overridePendingTransition(0,0)

                // hide progressbar
                ScDisplayUtils.hideProgressBar()
            })

            onCreate()
        }
    }

    override fun onDestroy() {
        loginViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginViewModel.onActivityResult(requestCode, resultCode, data)
    }
}
