package com.stevechulsdev.bansa.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.ScSnsGoogle
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.bansa.login.view.LoginActivity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_login.setOnClickListener {
            if(!LocalPreference.isLogin) {
                startActivityForResult<LoginActivity>(Constants.REQUEST_CODE_GO_LOGIN)
                overridePendingTransition(0,0)
            }
            else {
                ScDisplayUtils.toast(this, "이미 로그인 되어있습니다.")
                tv_info.text = "uid : ${LocalPreference.userUid}\nnickname : ${LocalPreference.userNickName}\nloginType : ${LocalPreference.loginType}"
            }
        }

        bt_logout.setOnClickListener {
            if(LocalPreference.isLogin) {
                if(LocalPreference.loginType == Constants.LoginType.GOOGLE.name) {
                    ScSnsGoogle.logout(this, object : ScSnsGoogle.LogoutInterface {
                        override fun success() {
                            ScDisplayUtils.toast(this@MainActivity, "로그아웃 되었습니다.")
                            tv_info.text = ""
                            Utils.setLocalUserDataIsLogin(this@MainActivity, false)
                            LocalPreference.isLogin = false
                        }

                        override fun fail(errorMsg: String) {
                            ScDisplayUtils.toast(this@MainActivity, "로그아웃 실패\nerror : $errorMsg")
                        }
                    })
                }
                else if(LocalPreference.loginType == Constants.LoginType.KAKAO.name){
                    KakaoManager().initKakao()

                    UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                        override fun onCompleteLogout() {
                            Utils.setLocalUserDataIsLogin(this@MainActivity, false)
                            LocalPreference.isLogin = false
                        }

                        // ui thread (update ui)
                        override fun onSuccess(result: Long?) {
                            ScDisplayUtils.toast(this@MainActivity, "로그아웃 되었습니다.")
                            tv_info.text = ""
                        }

                        override fun onFailure(errorResult: ErrorResult) {
                            ScDisplayUtils.toast(this@MainActivity, "로그아웃 실패\nerror : $errorResult")
                        }
                    })
                }
            }
            else {
                ScDisplayUtils.toast(this, "로그인을 해주세요.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Constants.REQUEST_CODE_GO_LOGIN -> {
                when(resultCode) {
                    Constants.RESULT_CODE_BACK_LOGIN -> {
                        Utils.setLocalUserDataIsLogin(this, true)
                        val isLogin = Utils.getLocalUserDataBoolean(this, Constants.LOCAL_DATA_KEY_USER_IS_LOGIN)
                        LocalPreference.isLogin = isLogin
                        tv_info.text = "uid : ${LocalPreference.userUid}\nnickname : ${LocalPreference.userNickName}\nloginType : ${LocalPreference.loginType}"
                    }
                }
            }
        }
    }
}
