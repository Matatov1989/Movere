package com.matatov.movere.utils

import android.util.Log
import com.matatov.movere.models.PushMessageModel
import com.squareup.okhttp.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException;


object FcmBuilderUtil {

    val MEDIA_TYPE_JSON: MediaType = MediaType.parse("application/json; charset=utf-8")
    private val SERVER_API_KEY =
        "AAAAyXHpznk:APA91bEy8R2DbRrX70tQsflbneWX9u_YVeZRkUmweAXrjaQ5xUJr8dsegOHj4ehMrDYehhYEU8MIhP-aDRYyMRoIYqf0nH33L-foQfSHcJFGxo376eBAta970_ch1Fdi3KPe19Epq95Z"
    private val CONTENT_TYPE = "Content-Type"
    private val APPLICATION_JSON = "application/json"
    private val AUTHORIZATION = "Authorization"
    private val AUTH_KEY = "key=$SERVER_API_KEY"
    private val FCM_URL = "https://fcm.googleapis.com/fcm/send"

    // json related keys
    const val KEY_TO = "to"
    const val KEY_NOTIFICATION = "notification"
    const val KEY_TITLE = "title"
    const val KEY_TEXT = "text"
    const val KEY_DATA = "data"
    const val KEY_USERNAME = "username"
    const val KEY_UID = "uid"
    const val KEY_FCM_TOKEN = "fcm_token"

    private var title: String? = null
    private var message: String? = null
    private var senderName: String? = null
    private var senderId: String? = null
    private var senderToken: String? = null
    private var receiverToken: String? = null


    fun sendMessageToUserDevice(pushNotif: PushMessageModel?, onComplete: (isSuccess: Boolean) -> Unit) {

        title = pushNotif!!.title
        message = pushNotif!!.message
        senderName = pushNotif!!.senderName
        senderId = pushNotif!!.senderId
        senderToken = pushNotif!!.senderToken
        receiverToken = pushNotif!!.receiverToken

        var requestBody: RequestBody? = null
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString())
            Log.d(ConstantsUtil.LOG_TAG, "send sms   "+message)
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d(ConstantsUtil.LOG_TAG, "send sms error   "+ e.printStackTrace())
        }

        val request = Request.Builder()
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(AUTHORIZATION, AUTH_KEY)
            .url(FCM_URL)
            .post(requestBody)
            .build()

        val call = OkHttpClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                Log.d(ConstantsUtil.LOG_TAG, "send sms onFailure   "+ e)
                onComplete(false)
            }

            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                Log.d(ConstantsUtil.LOG_TAG, "send sms onResponse   "+ response.toString())
                onComplete(true)
            }
        })
    }

    @Throws(JSONException::class)
    private fun getValidJsonBody(): JSONObject {
        val jsonObjectBody = JSONObject()
        jsonObjectBody.put(KEY_TO, receiverToken)
        val jsonObjectData = JSONObject()
        jsonObjectData.put(KEY_TITLE, title)    /*or text or image*/
        jsonObjectData.put(KEY_TEXT, message)
        jsonObjectData.put(KEY_USERNAME, senderName)
        jsonObjectData.put(KEY_UID, senderId)
        jsonObjectData.put(KEY_FCM_TOKEN, senderToken)
        jsonObjectBody.put(KEY_DATA, jsonObjectData)

        return jsonObjectBody
    }
}
