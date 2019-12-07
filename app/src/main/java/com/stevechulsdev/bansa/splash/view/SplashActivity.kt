package com.stevechulsdev.bansa.splash.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.intro.view.IntroFirstActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity<IntroFirstActivity>()
            finish()
            AnimationUtils().animFadeInFadeOut(this)
        }, Constants.SPLASH_TIME)
    }
}
