package com.stevechulsdev.bansa.intro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.Utils
import kotlinx.android.synthetic.main.activity_intro.*
import org.jetbrains.anko.startActivity

class IntroFirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusColor(this, "#f447a8")
        setContentView(R.layout.activity_intro)

        btn_next.setOnClickListener {
            startActivity<IntroSecondActivity>()
            AnimationUtils().animInRightToLeft(this)
        }
    }
}
