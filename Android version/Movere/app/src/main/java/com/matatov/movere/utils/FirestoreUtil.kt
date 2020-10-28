package com.matatov.movere.utils

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.matatov.movere.models.EventModel
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.ConstantsUtil.COLLECTION_CHATS
import com.matatov.movere.utils.ConstantsUtil.COLLECTION_EVENTS
import com.matatov.movere.utils.ConstantsUtil.COLLECTION_USERS
import com.matatov.movere.utils.ConstantsUtil.USER_GEO_HASH
import com.matatov.movere.utils.ConstantsUtil.USER_GEO_POINT
import com.matatov.movere.utils.ConstantsUtil.USER_ID
import com.matatov.movere.utils.ConstantsUtil.USER_NAME
import com.matatov.movere.utils.ConstantsUtil.USER_PHOTO
import com.matatov.movere.utils.ConstantsUtil.USER_TIME_STAMP
import com.matatov.movere.utils.ConstantsUtil.USER_TOKEN
import com.matatov.movere.utils.ConstantsUtil.USER_TYPE_VECHICLE
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.extension.setLocation
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener
import java.util.ArrayList

object FirestoreUtil {

    val db = Firebase.firestore
    var geoPoint: GeoPoint? = null
    var userModel: UserModel? = null

    //add new user to Firestore
    fun addNewUserToFirestore(
        firebaseUser: FirebaseUser?,
        onComplete: (isSuccess: Boolean) -> Unit
    ) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new Instance ID Token
                    val token = task.result!!.token
                    geoPoint = GeoPoint(0.0, 0.0)
                    val userModel = UserModel(
                        firebaseUser!!.uid,
                        firebaseUser!!.displayName,
                        firebaseUser!!.photoUrl.toString(),
                        0,
                        token,
                        "7zzzzzzzzz", //geoHash GeoPoint(0.0, 0.0)
                        geoPoint,
                        Timestamp.now()
                    )
                    // Add a new document with a generated ID
                    db.collection(COLLECTION_USERS)
                        .document(firebaseUser.uid)
                        .set(userModel)
                        .addOnSuccessListener {
                            onComplete(true)
                            return@addOnSuccessListener
                        }
                        .addOnFailureListener { e ->
                            onComplete(false)
                            return@addOnFailureListener
                        }
                } else {
                    onComplete(false)
                }
            })
    }

    // get all users to map from firestore
    fun getAllUsersFromFirestore(onComplete: (userList: ArrayList<UserModel>) -> Unit) {
        val userList = ArrayList<UserModel>()
        db.collection(COLLECTION_USERS)
            .whereNotEqualTo(USER_GEO_POINT, GeoPoint(0.0, 0.0))
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    var id = document.getString(USER_ID)
                    var name = document.getString(USER_NAME)
                    var photo = document.getString(USER_PHOTO)
                    var token = document.getString(USER_TOKEN)
                    var vechicle = document.getDouble(USER_TYPE_VECHICLE)!!.toInt()
                    var geoLocationHash = document.getString(USER_GEO_HASH)
                    var geoLocationPoint = document.getGeoPoint(USER_GEO_POINT)
                    var timestamp = document.getTimestamp(USER_TIME_STAMP)

                    userList.add(
                        UserModel(
                            id,
                            name,
                            photo,
                            vechicle,
                            token,
                            geoLocationHash,
                            geoLocationPoint,
                            timestamp
                        )
                    )

                }
                onComplete(userList)
                return@addOnSuccessListener
            }
            .addOnFailureListener { exception ->
                onComplete(userList)
                return@addOnFailureListener
                //           Log.d(LOG_TAG, "Error getting documents: ", exception)
            }
    }

    fun updateUserLocation(userId: String, geoPoint: GeoPoint?, onComplete: (isSuccess: Boolean) -> Unit) {

        val collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
        val geoFirestore = GeoFirestore(collectionRef)

        geoFirestore.setLocation(userId, geoPoint!!) { exception ->
            if (exception != null)
                onComplete(true)
            else
                onComplete(false)
        }
    }

    //get current user from firestore
    fun getCurrentUser(userId: String, onComplete: (user: UserModel) -> Unit) {
        db.collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener {

                onComplete(it.toObject(UserModel::class.java)!!)

                return@addOnSuccessListener
            }
    }

    fun getContacts(userId: String, onComplete: (userList: ArrayList<UserModel>) -> Unit) {
        val list = ArrayList<UserModel>()

        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_CHATS)
            .get()
            .addOnSuccessListener() { result ->
                for (document in result) {
                    db.collection(COLLECTION_USERS).document(document.id).get()
                        .addOnSuccessListener { result2 ->
                            list.add(result2.toObject(UserModel::class.java)!!)

                            // TODO : find another solution
                            if (result!!.size() == list.size) {
                                onComplete(list)
                                return@addOnSuccessListener
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                onComplete(list)
                return@addOnFailureListener
            }
    }

    fun addEventToFirestore(eventModel: EventModel, onComplete: (isSuccess: Boolean) -> Unit) {
        db.collection(COLLECTION_EVENTS).add(eventModel)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getUsersByRadiusLocalion(geoPoint: GeoPoint?, radius: Double, onComplete: (userList: ArrayList<UserModel>) -> Unit){
        val list = ArrayList<UserModel>()

        val collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
        val geoFirestore = GeoFirestore(collectionRef)

        //query around locationof user with a radius of 3.0 (30 km) kilometers
        val geoQuery = geoFirestore.queryAtLocation(GeoPoint(geoPoint!!.latitude, geoPoint!!.longitude), radius)

        geoQuery.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                if (!documentSnapshot.toObject(UserModel::class.java)!!.userId.equals(FirebaseAuth.getInstance().currentUser?.uid))
                    list.add(documentSnapshot.toObject(UserModel::class.java)!!)
            }

            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {
            }

            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
            }

            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
            }

            override fun onGeoQueryReady() {
                onComplete(list)
            }

            override fun onGeoQueryError(exception: Exception) {
            }
        })
    }

    fun getEventsByRadiusLocalion(geoPoint: GeoPoint?, radius: Double, onComplete: (userList: ArrayList<EventModel>) -> Unit){
        val list = ArrayList<EventModel>()

        val collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION_EVENTS)
        val geoFirestore = GeoFirestore(collectionRef)

        //query around locationof user with a radius of 3.0 (30 km) kilometers
        val geoQuery = geoFirestore.queryAtLocation(GeoPoint(geoPoint!!.latitude, geoPoint!!.longitude), radius)

        geoQuery.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                    list.add(documentSnapshot.toObject(EventModel::class.java)!!)
            }

            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {
            }

            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
            }

            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
            }

            override fun onGeoQueryReady() {
                onComplete(list)
            }

            override fun onGeoQueryError(exception: Exception) {
            }
        })
    }

    fun removeContactFromUserList(contactId: String?){
        db.collection(COLLECTION_USERS).document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection(COLLECTION_CHATS).document(contactId!!)
            .delete()
    }

    fun removeUserFromContactList(contactId: String?){
        db.collection(COLLECTION_USERS).document(contactId!!).collection(COLLECTION_CHATS).document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .delete()
    }
}
