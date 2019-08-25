package com.stevechulsdev.bansa.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.bansa.login.view.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_login.setOnClickListener {
            startActivityForResult<LoginActivity>(Constants.REQUEST_CODE_GO_LOGIN)
            overridePendingTransition(0,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Constants.REQUEST_CODE_GO_LOGIN -> {
                when(resultCode) {
                    Constants.RESULT_CODE_BACK_LOGIN -> {
                        tv_info.text = "uid : ${LocalPreference.userUid}\n nickname : ${LocalPreference.userNickName}"
                    }
                }
            }
        }
    }
}
