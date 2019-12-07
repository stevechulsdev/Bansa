package com.stevechulsdev.bansa.etc.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.stevechulsdev.bansa.R

class CustomLoginDialog(val mContext: Context): Dialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams().apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = 0.8f
        }

        window?.attributes = layoutParams

        setContentView(R.layout.dialog_custom_login)


    }
}