package com.matatov.movere.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matatov.movere.R
import com.matatov.movere.activities.MapActivity


class FirebaseMessagingService : FirebaseMessagingService() {

    val CHANNEL_ID = "101"

    var title: String? = null
    var text: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {

            title = remoteMessage.data["title"]
            text = remoteMessage.data["text"]

            createNotification()
        }
    }

    private fun createNotification() {
        val intent = Intent(this, MapActivity::class.java)
        //    intent.putExtra(Constants.ARG_CONTACT_DATA, receiverData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrate = longArrayOf(0, 300, 200, 300, 200, 300)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(vibrate)
            .setLights(Color.BLUE, 1, 0)
     //       .setDefaults(Notification.DEFAULT_LIGHTS)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = vibrate
            assert(notificationManager != null)
            notificationBuilder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        assert(notificationManager != null)
        notificationManager.notify(101, notificationBuilder.build())
    }
}
