package com.matatov.movere.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.matatov.movere.R
import com.matatov.movere.utils.FcmBuilderUtil
import com.matatov.movere.models.*
import com.matatov.movere.utils.ConstantsUtil
import com.matatov.movere.utils.FireMessageUtil
import com.matatov.movere.utils.FirestoreUtil
import com.matatov.movere.utils.StorageUtil
import com.valdesekamdem.library.mdtoast.MDToast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*


class ChatActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2

    private lateinit var currentChannelId: String
    private lateinit var senderUser: UserModel
    private lateinit var receiverUser: UserModel

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //     val toolbar: Toolbar = findViewById(R.id.toolbar)
        //     setSupportActionBar(toolbar)

        receiverUser =
            intent.getParcelableExtra<Parcelable>(ConstantsUtil.ARG_CONTACT_DATA) as UserModel
        //   userModel = intent.getParcelableExtra<Parcelable>(ArgumentUtil.ARG_CONTACT_DATA) as UserModel

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = receiverUser!!.userName


        //get sender data
        FirestoreUtil.getCurrentUser(FirebaseAuth.getInstance().currentUser?.uid.toString()) {
            senderUser = it
        }

        //    otherUserId = intent.getStringExtra(USER_ID)
        FireMessageUtil.getOrCreateChatChannel(receiverUser!!.userId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration = FireMessageUtil.addChatMessagesListener(
                channelId,
                this,
                this::updateRecyclerView
            )

            fabSendMessage.setOnClickListener {
                val messageToSend =
                    TextMessage(
                        editTextMessage.text.toString(),
                        Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        receiverUser.userId.toString(),
                        senderUser.userName.toString()
                    )

                FireMessageUtil.sendMessage(messageToSend, channelId) { isSuccess ->
                    if (isSuccess) {
                        sendMessage(editTextMessage.text.toString());
                        editTextMessage.setText("")
                    } else {
                        MDToast.makeText(
                            applicationContext,
                            getString(R.string.toastMessageError),
                            MDToast.LENGTH_LONG,
                            MDToast.TYPE_ERROR
                        ).show()
                    }
                }
            }

            fabSendImage.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    RC_SELECT_IMAGE
                )
            }
        }
    }

    private fun sendMessage(message: String) {
        val senderName: String = senderUser.userName!!
        val senderUid: String = senderUser.userId!!
        val senderToken: String = senderUser.userToken!!
        val receiverToken: String = receiverUser.userToken!!

        val pushNotif =
            PushMessageModel(senderName, message, senderName, senderUid, senderToken, receiverToken)

        FcmBuilderUtil.sendMessageToUserDevice(pushNotif) { isSuccess ->
            if (!isSuccess) {
                MDToast.makeText(
                    applicationContext,
                    getString(R.string.toastMessageError),
                    MDToast.LENGTH_LONG,
                    MDToast.TYPE_ERROR
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(
                contentResolver,
                selectedImagePath
            )

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                    ImageMessage(
                        imagePath, Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        receiverUser.userId.toString(),
                        senderUser.userName.toString()
                    )

                FireMessageUtil.sendMessage(messageToSend, currentChannelId){ isSuccess ->
                    if (isSuccess) {
                        sendMessage(getString(R.string.textImage));
                        editTextMessage.setText("")
                    } else {
                        MDToast.makeText(
                            applicationContext,
                            getString(R.string.toastMessageError),
                            MDToast.LENGTH_LONG,
                            MDToast.TYPE_ERROR
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, ContactsActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                startActivity(Intent(applicationContext, MapActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
