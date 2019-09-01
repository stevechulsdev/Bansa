package com.stevechulsdev.bansa.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val callOnClickLogin = MutableLiveData<Void>()
    val liveDataOnClickLogin: LiveData<Void>
        get() = callOnClickLogin

    private val callOnClickLogout = MutableLiveData<Void>()
    val liveDataOnClickLogout: LiveData<Void>
        get() = callOnClickLogout

    fun onClickLogin() {
        callOnClickLogin.postValue(null)
    }

    fun onClickLogout() {
        callOnClickLogout.postValue(null)
    }
}