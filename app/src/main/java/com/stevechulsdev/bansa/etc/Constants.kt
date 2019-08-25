package com.stevechulsdev.bansa.etc

import com.stevechulsdev.bansa.BuildConfig

object Constants {
    val IS_DEBUG = BuildConfig.DEBUG

    // SharedPreferences
    const val LOCAL_DATA_FILENAME = "UserData"          // File Name
    const val LOCAL_DATA_KEY_USER_UID = "uid"          // key UserData uid
    const val LOCAL_DATA_KEY_USER_NICKNAME = "nickname"     // key UserData nickname

    // Splash
    const val SPLASH_TIME = 2000L

    // Login
    const val GOOGLE_API_KEY = BuildConfig.GOOGLE_API_KEY






    // Request Code
    const val REQUEST_CODE_GOOGLE_LOGIN = 9000      // Google Login

    const val REQUEST_CODE_GO_LOGIN = 8000          // Go Login Activity



    // Result Code
    const val RESULT_CODE_BACK_LOGIN = 8001         // Back Login Activity
}