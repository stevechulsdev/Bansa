package com.stevechulsdev.bansa.splash.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.stevechulsdev.bansa.etc.AnimationUtils
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.intro.view.IntroFirstActivity
import org.jetbrains.anko.startActivity
import com.stevechulsdev.bansa.etc.Utils
import com.stevechulsdev.bansa.main.view.Main2Activity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusColor(this, "#f447a8")
        setContentView(com.stevechulsdev.bansa.R.layout.activity_splash)

        Handler().postDelayed({
            if(Utils.getLocalUserDataBoolean(this, Constants.LOCAL_DATA_KEY_USER_IS_LOGIN)) {
                startActivity<Main2Activity>()
            }
            else {
                startActivity<IntroFirstActivity>()
            }

            finish()
            AnimationUtils().animFadeInFadeOut(this)
        }, Constants.SPLASH_TIME)
    }
}
