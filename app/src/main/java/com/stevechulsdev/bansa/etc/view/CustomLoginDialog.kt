package com.stevechulsdev.bansa.etc.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.stevechulsdev.bansa.R
import kotlinx.android.synthetic.main.dialog_custom_login.*

class CustomLoginDialog(val mContext: Context, val customLoginDialogListener: (String) -> Unit): Dialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams().apply {
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = 0.8f
        }

        window?.attributes = layoutParams

        setContentView(R.layout.dialog_custom_login)

        btn_google.setOnClickListener {
            customLoginDialogListener.invoke("google")
            dismiss()
        }

        btn_kakao.setOnClickListener {
            customLoginDialogListener.invoke("kakao")
            dismiss()
        }
    }
}