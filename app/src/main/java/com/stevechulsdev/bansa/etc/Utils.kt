package com.stevechulsdev.bansa.etc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.main.view.MainActivity
import java.lang.Exception

object Utils {

    private fun setLocalUserDataString(context: Context, key: String, value: String) {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString(key, value).apply()
    }

    private fun setLocalUserDataBoolean(context: Context, key: String, value: Boolean) {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(key, value).apply()
    }

    fun getLocalUserDataString(context: Context, key: String): String? {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        try {
            return preference.getString(key, "")
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getLocalUserDataBoolean(context: Context, key: String): Boolean {
        val preference = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        try {
            return preference.getBoolean(key, false)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun setLocalUserDataUid(context: Context, value: String) {
        setLocalUserDataString(context, Constants.LOCAL_DATA_KEY_USER_UID, value)
    }

    fun setLocalUserDataNickName(context: Context, value: String) {
        setLocalUserDataString(context, Constants.LOCAL_DATA_KEY_USER_NICKNAME, value)
    }

    fun setLocalUserDataLoginType(context: Context, loginType: Constants.LoginType) {
        setLocalUserDataString(context, Constants.LOCAL_DATA_KEY_USER_LOGIN_TYPE, loginType.name)
    }

    fun setLocalUserDataIsLogin(context: Context, value: Boolean) {
        setLocalUserDataBoolean(context, Constants.LOCAL_DATA_KEY_USER_IS_LOGIN, value)
    }

    fun sendLocalPush(context: Context, title: String, contents: String) {
        // Pending Intent 부분은 목적에 맞게 사용해야 됨
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NotificationChannel("localChannel", "localPush", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = ""    // 채널에 대한 설명
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                vibrationPattern = longArrayOf(100, 200, 100, 200)
                notificationChannel.createNotificationChannel(this)
            }
        }

        val notificationBuilder = NotificationCompat.Builder(context, "localChannel")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(contents)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            notify(0, notificationBuilder.build())
        }
    }
}