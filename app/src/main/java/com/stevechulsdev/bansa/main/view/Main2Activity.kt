package com.stevechulsdev.bansa.main.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.kakao.KakaoManager
import com.stevechulsdev.bansa.login.view.LoginActivity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import kotlinx.android.synthetic.main.activity_intro_hash_tag.cl_main_layout
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.startActivityForResult

class Main2Activity : AppCompatActivity() {

    private val BACKBUTTON_TIME = 2000L
    private var mCheckDoubleBackTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusColor(this, "#ffffff")
        setContentView(R.layout.activity_main2)

        supportFragmentManager.beginTransaction().replace(R.id.fl_content_layout, MainFragment.newInstance()).commit()

        if(!LocalPreference.isLogin) {
            startActivityForResult<LoginActivity>(Constants.REQUEST_CODE_GO_LOGIN)
        }

        // Home
        ll_home_layout.setOnClickListener {
            iv_home.setImageResource(R.drawable.menu_bottom_home_on)
            tv_home.setTextColor(Color.parseColor("#ff6c88"))

            iv_search.setImageResource(R.drawable.menu_bottom_search_off)
            tv_search.setTextColor(Color.parseColor("#000000"))

            iv_bookMark.setImageResource(R.drawable.menu_bottom_jewelry_off)
            tv_bookMark.setTextColor(Color.parseColor("#000000"))
            supportFragmentManager.beginTransaction().replace(R.id.fl_content_layout, MainFragment.newInstance()).commit()
        }

        // BookMark
        ll_jewelry_layout.setOnClickListener {
            iv_bookMark.setImageResource(R.drawable.menu_bottom_jewelry_on)
            tv_bookMark.setTextColor(Color.parseColor("#ff6c88"))

            iv_home.setImageResource(R.drawable.menu_bottom_home_off)
            tv_home.setTextColor(Color.parseColor("#000000"))

            iv_search.setImageResource(R.drawable.menu_bottom_search_off)
            tv_search.setTextColor(Color.parseColor("#000000"))
            supportFragmentManager.beginTransaction().replace(R.id.fl_content_layout, BookMarkFragment.newInstance()).commit()
        }
    }

    // Back Key 누를 경우
    // 2초 동안 두 번 눌러야 앱 종료
    override fun onBackPressed() {
        if(System.currentTimeMillis() - mCheckDoubleBackTime >= BACKBUTTON_TIME) {
            mCheckDoubleBackTime = System.currentTimeMillis()
            ScDisplayUtils.snackBar(cl_main_layout, "한번 더 누르면 종료됩니다.")
        }
        else if(System.currentTimeMillis() - mCheckDoubleBackTime < BACKBUTTON_TIME) {
            super.onBackPressed()
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

                        if(LocalPreference.loginType == Constants.LoginType.KAKAO.name) {
                            KakaoManager.removeCallback()
                        }
                    }
                }
            }
            5656 -> {
                if(resultCode == 6565) {
                    iv_home.setImageResource(R.drawable.menu_bottom_home_on)
                    tv_home.setTextColor(Color.parseColor("#ff6c88"))

                    iv_search.setImageResource(R.drawable.menu_bottom_search_off)
                    tv_search.setTextColor(Color.parseColor("#000000"))

                    iv_bookMark.setImageResource(R.drawable.menu_bottom_jewelry_off)
                    tv_bookMark.setTextColor(Color.parseColor("#000000"))

                    supportFragmentManager.beginTransaction().replace(R.id.fl_content_layout, MainFragment.newInstance()).commitAllowingStateLoss()
                }
            }
        }
    }
}
