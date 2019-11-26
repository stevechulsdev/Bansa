package com.stevechulsdev.bansa.intro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stevechulsdev.bansa.R
import kotlinx.android.synthetic.main.activity_intro_second.*
import org.jetbrains.anko.startActivity

class IntroSecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_second)

        btn_next.setOnClickListener {
            startActivity<IntroHashTagActivity>()
        }
    }
}
