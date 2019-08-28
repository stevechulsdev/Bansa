package com.stevechulsdev.bansa.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stevechulsdev.bansa.R
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.sclog.ScLog

class FCMService: FirebaseMessagingService() {

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
        ScLog.e(Constants.IS_DEBUG, "FCM Token : $fcmToken")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.data.isNotEmpty()) sendNotification(remoteMessage)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val contents = remoteMessage.data["contents"]

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NotificationChannel("", "", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = ""    // 채널에 대한 설명
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                vibrationPattern = longArrayOf(100, 200, 100, 200)
                notificationChannel.createNotificationChannel(this)
            }
        }

        val notificationBuilder = NotificationCompat.Builder(this, "")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(contents)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            notify(9999, notificationBuilder.build())
        }
    }
}