package com.stevechulsdev.bansa.etc

import android.app.Activity
import com.stevechulsdev.bansa.R

class AnimationUtils {

    fun animInRightToLeft(activity: Activity) {
        activity.overridePendingTransition(R.anim.in_right_to_left_r, R.anim.in_right_to_left_l)
    }

    fun animOutRightToLeft(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun animOutLeftToRight(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun animFadeInFadeOut(activity: Activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}