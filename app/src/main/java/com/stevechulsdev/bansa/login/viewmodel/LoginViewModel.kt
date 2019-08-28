package com.stevechulsdev.bansa.login.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stevechulsdev.scdisplayutils.ScDisplayUtils
import com.stevechulsdev.sclog.ScLog

class LoginViewModel: ViewModel() {

    private val callOnClickGoBack = MutableLiveData<Void>()
    val liveDataOnClickGoBack: LiveData<Void>
        get() = callOnClickGoBack

    fun onCreate() {

    }

    fun onClickGoBack() {
        ScLog.e(true, "Click!!!!!!!!!!!!!!!!!!!!")
        callOnClickGoBack.postValue(null)
    }
}