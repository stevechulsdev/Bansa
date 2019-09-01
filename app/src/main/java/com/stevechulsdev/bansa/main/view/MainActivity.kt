package com.stevechulsdev.bansa.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.stevechulsdev.bansa.BR
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.firebase.ScSnsGoogle
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.bansa.login.view.LoginActivity
import com.stevechulsdev.bansa.main.viewmodel.MainViewModel
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult

class MainActivity : AppCompatActivity() {

    private val mContext = this
    private var mainViewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding viewModel
        var mViewDataBinding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_main)
        mViewDataBinding.setVariable(BR.vm, mainViewModel)

        // Set Observer
        mainViewModel.apply {
            liveDataOnClickLogin.observe(mContext, Observer {
                if(!LocalPreference.isLogin) {
                    startActivityForResult<LoginActivity>(Constants.REQUEST_CODE_GO_LOGIN)
                    overridePendingTransition(0,0)
                }
                else {
                    ScDisplayUtils.toast(mContext, "이미 로그인 되어있습니다.")
                    tv_info.text = "uid : ${LocalPreference.userUid}\nnickname : ${LocalPreference.userNickName}\nloginType : ${LocalPreference.loginType}"
                }
            })

            liveDataOnClickLogout.observe(mContext, Observer {
                if(LocalPreference.isLogin) {
                    if(LocalPreference.loginType == Constants.LoginType.GOOGLE.name) {
                        ScSnsGoogle.initLogin(mContext, Constants.GOOGLE_API_KEY)

                        ScSnsGoogle.logout(mContext, object : ScSnsGoogle.LogoutInterface {
                            override fun success() {
                                ScDisplayUtils.toast(mContext, "로그아웃 되었습니다.")
                                tv_info.text = ""
                                Utils.setLocalUserDataIsLogin(mContext, false)
                                LocalPreference.isLogin = false
                            }

                            override fun fail(errorMsg: String) {
                                ScDisplayUtils.toast(mContext, "로그아웃 실패\nerror : $errorMsg")
                            }
                        })
                    }
                    else if(LocalPreference.loginType == Constants.LoginType.KAKAO.name){
                        KakaoManager.initKakao()

                        UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                            override fun onCompleteLogout() {
                                runOnUiThread {
                                    Utils.setLocalUserDataIsLogin(mContext, false)
                                    LocalPreference.isLogin = false
                                    ScDisplayUtils.toast(mContext, "로그아웃 되었습니다.")
                                    tv_info.text = ""
                                }
                            }

//                            // ui thread (update ui)
//                            override fun onSuccess(result: Long?) {
//                                ScDisplayUtils.toast(mContext, "로그아웃 되었습니다.")
//                                tv_info.text = ""
//                            }

                            override fun onFailure(errorResult: ErrorResult) {
                                ScDisplayUtils.toast(mContext, "로그아웃 실패\nerror : $errorResult")
                            }
                        })
                    }
                }
                else {
                    ScDisplayUtils.toast(mContext, "로그인을 해주세요.")
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Constants.REQUEST_CODE_GO_LOGIN -> {
                when(resultCode) {
                    Constants.RESULT_CODE_BACK_LOGIN -> {
                        Utils.setLocalUserDataIsLogin(mContext, true)
                        val isLogin = Utils.getLocalUserDataBoolean(mContext, Constants.LOCAL_DATA_KEY_USER_IS_LOGIN)
                        LocalPreference.isLogin = isLogin
                        tv_info.text = "uid : ${LocalPreference.userUid}\nnickname : ${LocalPreference.userNickName}\nloginType : ${LocalPreference.loginType}"

                        if(LocalPreference.loginType == Constants.LoginType.KAKAO.name) {
//                            KakaoManager.callGetUserDataSuccess = null
                            KakaoManager.removeCallback()
                        }
                    }
                }
            }
        }
    }
}
