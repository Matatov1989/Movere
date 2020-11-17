package com.matatov.movere.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.clustering.ClusterManager
import com.matatov.movere.R
import com.matatov.movere.cluster.ClusterMarker
import com.matatov.movere.cluster.MyClusterManagerRenderer
import com.matatov.movere.models.UserModel
import com.matatov.movere.services.SendMessagesService
import com.matatov.movere.utils.ConstantsUtil.ARG_CONTACT_DATA
import com.matatov.movere.utils.ConstantsUtil.LOG_TAG
import com.matatov.movere.utils.FirestoreUtil
import com.matatov.movere.utils.FirestoreUtil.updateUserStatus
import com.matatov.movere.utils.FirestoreUtil.updateUserTimestamp
import com.valdesekamdem.library.mdtoast.MDToast
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*


class MapActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback,
    ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker> {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mClusterManagerRenderer: MyClusterManagerRenderer? = null
    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private val mClusterMarkers = ArrayList<ClusterMarker>()
    private var mapBoundary: LatLngBounds? = null

    var userModel: UserModel? = null

    var imageUser: ImageView? = null
    var textUserName: TextView? = null
    var textBtnProfileUser: TextView? = null

    var flagGetData: Boolean? = false

    private val PERMISSION_LOCATION = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        Log.d(LOG_TAG, "onCreate   ")


        //get user data from SplashActivity or AuthenticationActivity
        if (intent.hasExtra(SplashActivity::class.java.canonicalName))
            userModel = intent.getParcelableExtra<Parcelable>(SplashActivity::class.java.canonicalName) as UserModel
        else if (intent.hasExtra(AuthenticationActivity::class.java.canonicalName))
            userModel = intent.getParcelableExtra<Parcelable>(AuthenticationActivity::class.java.canonicalName) as UserModel
        else
            flagGetData = true;

        //     Log.d(LOG_TAG, "onCreate   " + userModel)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        //button update user location
        fabLocation.setOnClickListener {
            checkPermissionLocation()
        }

        //button send SOS
        fabSendSOS.setOnClickListener {
            dialogSendSOS()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart   " )
        initMap()

        //get user data from Firestore if start activity from other activities (not SplashActivity or not AuthenticationActivity)
        if (flagGetData!!){
               FirestoreUtil.getCurrentUser(FirebaseAuth.getInstance().currentUser?.uid.toString()) { user ->
                userModel = user
                initNavHeader()
            }
        }
        else {
            initNavHeader()
            updateUserStatus(true)  // TODO : check it here and think about if in other activities
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume   " )



        //get all users
        FirestoreUtil.getAllUsersFromFirestore() { list ->
            for (user in list)
                addUserToMapMarker(user)

            if (!userModel!!.g.equals("7zzzzzzzzz"))
                setCameraView()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG_TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy")

        updateUserTimestamp()
        updateUserStatus(false)
    }

    // TODO : set this method to all activities with update user data
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d(LOG_TAG, "onUserLeaveHint")

        updateUserTimestamp()
        updateUserStatus(false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(LOG_TAG, "onMapReady")
        mMap = googleMap
        //      mMap.setOnInfoWindowClickListener(this);
        //      mMap.setOnInfoWindowClickListener(this);
        if (mClusterManager == null) {
            mClusterManager = ClusterManager(applicationContext, mMap)
        }
        if (mClusterManagerRenderer == null) {
            mClusterManagerRenderer = MyClusterManagerRenderer(
                this,
                mMap,
                mClusterManager
            )
            mClusterManager!!.setRenderer(mClusterManagerRenderer)
        }
        mClusterManager!!.setOnClusterItemInfoWindowClickListener(this)
        mMap.setOnInfoWindowClickListener(mClusterManager)
    }

    //initialization map
    fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //initialization navigation header
    fun initNavHeader() {
        val hView: View = nav_view.getHeaderView(0)

        imageUser = hView.findViewById<ImageView>(R.id.imageUser)
        textUserName = hView.findViewById<TextView>(R.id.textUserName)
        textBtnProfileUser = hView.findViewById<TextView>(R.id.textBtnProfileUser)

        //set user photo
        val requestOptions: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_baseline_icon_user_24)
            .error(R.drawable.ic_baseline_icon_user_24)

        Glide.with(this@MapActivity)
            .setDefaultRequestOptions(requestOptions)
            .load(Uri.parse(userModel!!.userUriPhoto))
            .into(imageUser!!)

        textUserName!!.text = userModel!!.userName

        textBtnProfileUser!!.setOnClickListener {
            textBtnProfileUser!!.setTextColor(resources.getColor(R.color.colorWhiteDark))

            // TODO : create class or dialod

        }
    }

    fun setCameraView() {
        // Set a boundary to start
        val bottomBoundary: Double = userModel!!.l!!.latitude - .1
        val leftBoundary: Double = userModel!!.l!!.longitude - .1
        val topBoundary: Double = userModel!!.l!!.latitude + .1
        val rightBoundary: Double = userModel!!.l!!.longitude + .1
        mapBoundary =
            LatLngBounds(LatLng(bottomBoundary, leftBoundary), LatLng(topBoundary, rightBoundary))
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0))
    }

    private fun dialogSendSOS() {
        val adb = AlertDialog.Builder(this@MapActivity)
        adb.setCancelable(false)
        adb.setTitle(R.string.textTitleRemind)
        adb.setMessage(R.string.textRadiusEventSOS)
        adb.setPositiveButton(R.string.btnOK) { dialog, which ->
            dialog.dismiss()
            sendEventSOS()
        }
        adb.show()
    }

    private fun sendEventSOS() {
        if (userModel!!.l!!.latitude == 0.0 && userModel!!.l!!.longitude == 0.0) {
            // TODO : create dialog or snike bat or toast
        } else {
            startService(
                Intent(this, SendMessagesService::class.java)
                    .putExtra(MapActivity::class.java.canonicalName, userModel)
            )
        }
    }

    private fun updateLocationUser() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                Log.d(LOG_TAG, "updateLocationUser   " + location)

                if (location != null) {

                    var geoPoint = GeoPoint(location!!.latitude, location!!.longitude)

                    userModel!!.l = geoPoint

                    FirestoreUtil.updateUserLocation(
                        userModel!!.userId.toString(),
                        geoPoint
                    ) { isSuccess ->

                        Log.d(LOG_TAG, "updateLocationUser isSuccess  " + isSuccess)

                        if (isSuccess)
                            setCameraView()
                        else
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

    override fun onClusterItemInfoWindowClick(clusterMarker: ClusterMarker?) {
        if (clusterMarker!!.getSnippet() == "This is you") {
            //clusterMarker.
        } else {
            val intent = Intent(applicationContext, InfoUserActivity::class.java)
            intent.putExtra(ARG_CONTACT_DATA, clusterMarker!!.user)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.navContacts -> {
                startActivity(Intent(applicationContext, ContactsActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navEvents -> {
                startActivity(
                    Intent(applicationContext, EventsActivity::class.java)
                        .putExtra(MapActivity::class.java.canonicalName, userModel)
                )
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navAboutProgram -> {
                startActivity(Intent(applicationContext, AboutProgramActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.navShare -> {
                shareApp()
            }

            R.id.navFeedback -> {
                feedbackApp()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //share application
    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=com.matatov.movere"
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    //feedback application
    private fun feedbackApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("https://play.google.com/store/apps/details?id=com.matatov.movere")
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun addUserToMapMarker(userData: UserModel) {
        //    mClusterMarkers.clear();
        if (mMap != null) {
            try {
                var snippet = ""
                snippet = if (userData.userId.equals(FirebaseAuth.getInstance().uid)) {
                    "This is you"
                } else {
                    "Determine route to " + userData.userName.toString() + "?"
                }
                var avatar: Int = R.drawable.ic_baseline_child_friendly_24 // set the default avatar
                when (userData.userTypeVehicle) {
                    -1 -> avatar = R.drawable.ic_baseline_help_outline_24
                    0 -> avatar = R.drawable.ic_baseline_child_friendly_24
                    1 -> avatar = R.drawable.ic_baseline_motorcycle_24
                    2 -> avatar = R.drawable.ic_baseline_directions_car_24
                    3 -> avatar = R.drawable.ic_baseline_airport_shuttle_24
                }
                try {
                    avatar = userData.userUriPhoto!!.toInt()
                } catch (e: NumberFormatException) {
                }
                val newClusterMarker = ClusterMarker(
                    LatLng(
                        userData.l!!.getLatitude(),
                        userData.l!!.getLongitude()
                    ),
                    userData.userName,
                    snippet,
                    avatar,
                    userData
                )
                mClusterManager!!.addItem(newClusterMarker)
                mClusterMarkers.add(newClusterMarker)
            } catch (e: NullPointerException) {
            }
            mClusterManager!!.cluster()

        }
    }

    private fun checkPermissionLocation() {

        if (ContextCompat.checkSelfPermission(
                this@MapActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // You can use the API that requires the permission.
            updateLocationUser()

        } else {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(
                this@MapActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationUser()
                } else {
                    dialogPermossionDescription()
                }
                return
            }
        }
    }

    private fun dialogPermossionDescription() {
        val adb = AlertDialog.Builder(this@MapActivity)
        adb.setCancelable(false)
        adb.setMessage(R.string.dialogPermissionLocation)
        adb.setPositiveButton(R.string.btnOK) { dialog, which ->
            dialog.dismiss()
        }
        adb.show()
    }
}
