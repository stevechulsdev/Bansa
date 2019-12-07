package com.stevechulsdev.bansa.intro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.main.view.Main2Activity
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import kotlinx.android.synthetic.main.activity_intro_hash_tag.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class IntroHashTagActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusColor(this, "#ffffff")
        setContentView(R.layout.activity_intro_hash_tag)

        tv_skip.setOnClickListener {
            startActivity(intentFor<Main2Activity>().newTask().clearTask())
            finish()
            AnimationUtils().animFadeInFadeOut(this)
        }

        btn_start.setOnClickListener {
            startActivity(intentFor<Main2Activity>().newTask().clearTask())
            finish()
            AnimationUtils().animFadeInFadeOut(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AnimationUtils().animOutLeftToRight(this)
    }
}
