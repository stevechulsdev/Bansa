package com.stevechulsdev.bansa.main.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.LocalPreference
import kotlinx.android.synthetic.main.activity_my_info.*

class MyInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        tv_nickName.text = "닉네임 : ${LocalPreference.userNickName}"

        cl_help_layout.setOnClickListener {
            try {
                val phoneModel = Build.MODEL
                val phoneOS = Build.VERSION.RELEASE

                val uriText = "mailto:stevechulsdev@gmail.com" +
                        "?subject=" + Uri.encode("반사 문의하기") +
                        "&body=" + Uri.encode("문의 내용을 입력해주세요.<br><br>" +
                        "보다 정확한 문제해결을 위해 아래 정보를 함께 전송합니다.<br><br>" +
                        "핸드폰 기종 : $phoneModel<br>" +
                        "핸드폰 OS : $phoneOS<br>" +
                        "식별값 : ${LocalPreference.userUid} <br>" +
                        "닉네임 : ${LocalPreference.userNickName}<br>" +
                        "로그인 유형 : ${LocalPreference.loginType}<br><br>" +
                        "내용 입력 : ")

                val uri = Uri.parse(uriText)

                val sendIntent = Intent(Intent.ACTION_SENDTO)
                sendIntent.data = uri
                startActivity(Intent.createChooser(sendIntent, "문의하기"))
            }
            catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AnimationUtils().animOutLeftToRight(this)
    }
}
