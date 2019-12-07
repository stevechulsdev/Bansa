package com.stevechulsdev.bansa.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.etc.view.CustomLoginDialog
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import kotlinx.android.synthetic.main.activity_intro_hash_tag.*

class Main2Activity : AppCompatActivity() {

    private val BACKBUTTON_TIME = 2000L
    private var mCheckDoubleBackTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusColor(this, "#ffffff")
        setContentView(R.layout.activity_main2)

        supportFragmentManager.beginTransaction().replace(R.id.fl_content_layout, MainFragment.newInstance()).commit()

        CustomLoginDialog(this).show()
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
}
