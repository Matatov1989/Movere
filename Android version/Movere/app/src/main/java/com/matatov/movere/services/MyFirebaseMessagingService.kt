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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matatov.movere.R
import com.matatov.movere.activities.ChatActivity
import com.matatov.movere.activities.MapActivity
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.ConstantsUtil
import com.matatov.movere.utils.ConstantsUtil.ARG_CONTACT_DATA
import com.matatov.movere.utils.ConstantsUtil.LOG_TAG
import com.matatov.movere.utils.FcmBuilderUtil.KEY_FCM_TOKEN
import com.matatov.movere.utils.FcmBuilderUtil.KEY_TEXT
import com.matatov.movere.utils.FcmBuilderUtil.KEY_TITLE
import com.matatov.movere.utils.FcmBuilderUtil.KEY_UID
import com.matatov.movere.utils.FireMessageUtil

class MyFirebaseMessagingService: FirebaseMessagingService() {

    val CHANNEL_ID = "101"

    var title: String? = null
    var text: String? = null
    var senderId: String? = null
    var senderToken: String? = null

    var senderUser: UserModel? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {

            title = remoteMessage.data[KEY_TITLE]
            text = remoteMessage.data[KEY_TEXT]
            senderId = remoteMessage.data[KEY_UID]
            senderToken = remoteMessage.data[KEY_FCM_TOKEN]

            // TODO : fix it
            senderUser = UserModel(senderId, title, "", -1, senderToken, false,"7zzzzzzzzz", GeoPoint(0.0, 0.0), Timestamp.now())

//            senderUser = UserModel(senderId, title, senderToken)

            createNotification()
        }

        remoteMessage.notification?.let {
            Log.d(LOG_TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(LOG_TAG, "onDeletedMessages")
    }

    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "onNewToken 1 token: $token")

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "Fetching FCM registration token failed", task.exception)

                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token1 = task.result

            Log.d(LOG_TAG, "onNewToken 2 token: $token1")

            if (FirebaseAuth.getInstance().currentUser != null)
                addTokenToFirestore(token)

        })
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FireMessageUtil.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                //   tokens.add(newRegistrationToken)
                // TODO : check update token via userId
                FireMessageUtil.setFCMRegistrationTokens(tokens)
            }
        }
    }

    private fun createNotification() {

        //TODO : fix PendingIntent
        val intent = Intent(applicationContext, ChatActivity::class.java).putExtra(ARG_CONTACT_DATA, senderUser)

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
