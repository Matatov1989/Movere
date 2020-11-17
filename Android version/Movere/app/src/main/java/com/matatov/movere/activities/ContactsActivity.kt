package com.matatov.movere.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.matatov.movere.R
import com.matatov.movere.adapters.ContactsRecyclerAdapter
import com.matatov.movere.interfaces.OnResultClickPosition
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.ConstantsUtil
import com.matatov.movere.utils.ConstantsUtil.ARG_CONTACT_DATA
import com.matatov.movere.utils.ConstantsUtil.LOG_TAG
import com.matatov.movere.utils.FireMessageUtil
import com.matatov.movere.utils.FirestoreUtil
import com.valdesekamdem.library.mdtoast.MDToast
import kotlinx.android.synthetic.main.activity_contacts.*


class ContactsActivity : AppCompatActivity(), OnResultClickPosition.Contact {

    var recyclerAdapter: ContactsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerContact!!.isClickable = true

        //get all contacts (friends)
        FirestoreUtil.getContacts(FirebaseAuth.getInstance().currentUser?.uid.toString()){ contactList ->

            //sort list by alphabet
            contactList.sortBy { it.userName }

            recyclerAdapter = ContactsRecyclerAdapter(this, contactList)

            recyclerContact!!.adapter = recyclerAdapter
            recyclerContact!!.layoutManager = LinearLayoutManager(this@ContactsActivity)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, MapActivity::class.java))
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

    override fun onClickPosition(contact: UserModel) {
        startActivity(Intent(applicationContext, ChatActivity::class.java).putExtra(ARG_CONTACT_DATA, contact))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onClickPositionLongPress(contact: UserModel) {

        Log.d(LOG_TAG, "long press   ")

        // TODO : make with checking and uodate list
        //get chatId via contacntId
        FireMessageUtil.getOrCreateChatChannel(contact.userId){ chatId ->
            //remove chat from Chat collections
            FireMessageUtil.removeChat(chatId){ isSuccess ->
                if (isSuccess){
                    FirestoreUtil.removeContactFromUserList(contact.userId)
                    FirestoreUtil.removeUserFromContactList(contact.userId)

                    MDToast.makeText(
                        applicationContext,
                        getString(R.string.toastRemoveContact),
                        MDToast.LENGTH_LONG,
                        MDToast.TYPE_SUCCESS
                    ).show()

                }
            }
        }
    }
}
