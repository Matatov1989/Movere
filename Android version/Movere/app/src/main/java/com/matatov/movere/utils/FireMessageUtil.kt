package com.matatov.movere.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.matatov.movere.interfaces.Message
import com.matatov.movere.items.ImageMessageItem
import com.matatov.movere.items.TextMessageItem
import com.matatov.movere.models.ChatChannel
import com.matatov.movere.models.ImageMessage
import com.matatov.movere.models.TextMessage
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.ConstantsUtil.CHANNEL_ID
import com.matatov.movere.utils.ConstantsUtil.COLLECTION_CHAT_CHANNELS
import com.matatov.movere.utils.ConstantsUtil.COLLECTION_USERS
import com.matatov.movere.utils.ConstantsUtil.COLLECTION_CHATS
import com.matatov.movere.utils.ConstantsUtil.MESSAGES
import com.xwray.groupie.kotlinandroidextensions.Item

object FireMessageUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(COLLECTION_USERS+"/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection(COLLECTION_CHAT_CHANNELS)


    fun getOrCreateChatChannel(otherUserId: String?, onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection(COLLECTION_CHATS)
            .document(otherUserId!!).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it[CHANNEL_ID] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection(COLLECTION_CHATS)
                    .document(otherUserId)
                    .set(mapOf(CHANNEL_ID to newChannel.id))

                firestoreInstance.collection(COLLECTION_USERS).document(otherUserId)
                    .collection(COLLECTION_CHATS)
                    .document(currentUserId)
                    .set(mapOf(CHANNEL_ID to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection(MESSAGES)
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == ConstantsUtil.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String, onComplete: (isSuccess: Boolean) -> Unit) {
        chatChannelsCollectionRef.document(channelId)
            .collection(MESSAGES)
            .add(message)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: String) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(UserModel::class.java)!!
            onComplete(user!!.userToken.toString())
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: String) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }

    fun removeChat(chatId: String?, onComplete: (isSuccess: Boolean) -> Unit){
        FirestoreUtil.db.collection(COLLECTION_CHAT_CHANNELS).document(chatId!!)
            .delete()
            .addOnSuccessListener{ onComplete(true) }
            .addOnFailureListener{ onComplete(false) }
    }
}
