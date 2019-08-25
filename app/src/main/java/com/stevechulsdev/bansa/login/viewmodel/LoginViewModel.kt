package com.stevechulsdev.bansa.login.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    private val callOnClickGoBack = MutableLiveData<Void>()
    val liveDataOnClickGoBack: LiveData<Void>
        get() = callOnClickGoBack

    fun onCreate() {

    }

    fun onClickGoBack(view: View) {
        callOnClickGoBack.postValue(null)
    }
}