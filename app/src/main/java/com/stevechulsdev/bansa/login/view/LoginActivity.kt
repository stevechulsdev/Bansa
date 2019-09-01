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
import com.kakao.util.exception.KakaoException
import com.stevechulsdev.bansa.BR
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.ScSnsGoogle
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.bansa.login.viewmodel.LoginViewModel
import com.stevechulsdev.bansa.main.view.MainActivity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    private var mContext = this
    private var loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding viewModel
        var mViewDataBinding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_login)
        mViewDataBinding.setVariable(BR.vm, loginViewModel)

        // Set Observer
        loginViewModel.apply {

            // "구글 로그인" 클릭
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

            // "카카오 로그인" 클릭
            liveDataOnClickKakaoLogin.observe(mContext, Observer {
                ScDisplayUtils.showProgressBar(mContext)

                KakaoManager.setCallback(object : KakaoManager.KakaoSessionListener {
                    override fun onSessionOpened() {
                        loginViewModel.kakaoLogin()
                    }

                    override fun onSessionOpenFailed(exception: KakaoException?) {
                        exception?.let {
                            ScLog.e(true, "SessionCallback onSessionOpenFailed error : $exception")
                        }
                        ScDisplayUtils.hideProgressBar()
                    }
                }).apply {
                    open(AuthType.KAKAO_TALK, mContext)
                }
            })

            // "둘러보기" 클릭
            liveDataOnClickGoBack.observe(mContext, Observer {
                finish()
                overridePendingTransition(0,0)
            })

            liveDataMoveMainActivity.observe(mContext, Observer {
                startActivity<MainActivity>()
                finish()
                overridePendingTransition(0,0)
            })

            // 구글 로그인 후, 로그인된 사용자 정보 가져와서 Firebase DB 저장
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

            // SharePreference에 id, nickName, loginType 저장 -> UI 업데이트
            liveDataSetLocalData.observe(mContext, Observer { isNewMember ->

                Utils.setLocalUserDataUid(this@LoginActivity, LocalPreference.userUid)
                Utils.setLocalUserDataNickName(this@LoginActivity, LocalPreference.userNickName)
                Utils.setLocalUserDataLoginType(this@LoginActivity, LocalPreference.loginType)

                if(isNewMember) Toast.makeText(this@LoginActivity, "가입을 환영합니다.", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this@LoginActivity, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show()

                // LoginActivity 닫히면서, MainActivity Call onActivityResult()
                setResult(Constants.RESULT_CODE_BACK_LOGIN)
                finish()
                overridePendingTransition(0,0)

                // hide progressbar
                ScDisplayUtils.hideProgressBar()
            })

            liveDataGetUserDataSuccess.observe(mContext, Observer { result ->
                setUserData(result.id.toString(), Constants.LoginType.KAKAO)
            })

            // Call LoginViewModel onCreate
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
