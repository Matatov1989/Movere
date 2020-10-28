package com.matatov.movere.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.matatov.movere.R
import com.matatov.movere.activities.MapActivity
import com.matatov.movere.models.EventModel
import com.matatov.movere.models.PushMessageModel
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.FcmBuilderUtil
import com.matatov.movere.utils.FirestoreUtil
import com.valdesekamdem.library.mdtoast.MDToast
import java.util.*

class SendMessagesService : Service() {

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val senderUser = intent!!.getParcelableExtra<Parcelable>(MapActivity::class.java.canonicalName) as UserModel

        val cal = Calendar.getInstance()
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        //   if (minute >= 15)
        cal.set(Calendar.HOUR_OF_DAY, hours + 1)

        val timeStampStop = Timestamp(Date(cal.timeInMillis))

        val eventModel = EventModel(
            getString(R.string.textSOS),
            getDrawable(R.drawable.ic_baseline_help_outline_24).toString(), // TODO : fix it
            getString(R.string.textUserNeedsHelp, senderUser!!.userName),
            senderUser!!.g,
            senderUser!!.l,
            timeStampStop,
            senderUser!!.userId
        )
        //add event to Firestore
        FirestoreUtil.addEventToFirestore(eventModel) { isSuccess ->
            if (isSuccess) {
                //get users by radius
                FirestoreUtil.getUsersByRadiusLocalion(senderUser!!.l, 30.0){ list ->

                    var cnt = 0
                    for (receiverUser in list) {
                        val senderName: String = senderUser.userName!!
                        val senderUid: String = senderUser.userId!!
                        val senderToken: String = senderUser.userToken!!
                        val receiverToken: String = receiverUser.userToken!!
                        val message = getString(R.string.textUserNeedsHelp, senderUser!!.userName)

                        val pushNotif =
                            PushMessageModel(senderName, message, senderName, senderUid, senderToken, receiverToken)

                        cnt++
                        FcmBuilderUtil.sendMessageToUserDevice(pushNotif){ isSuccess ->
                            if (isSuccess){
                               if (cnt == list.size)
                                   stopSelf()
                            }
                            else{
                                MDToast.makeText(
                                    applicationContext,
                                    getString(R.string.toastError),
                                    MDToast.LENGTH_LONG,
                                    MDToast.TYPE_ERROR
                                ).show()
                            }
                        }
                    }
                }
            }
            else
                MDToast.makeText(
                    applicationContext,
                    getString(R.string.toastError),
                    MDToast.LENGTH_LONG,
                    MDToast.TYPE_ERROR
                ).show()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        MDToast.makeText(
            applicationContext,
            getString(R.string.toastSendSOS),
            MDToast.LENGTH_LONG,
            MDToast.TYPE_SUCCESS
        ).show()
    }
}
