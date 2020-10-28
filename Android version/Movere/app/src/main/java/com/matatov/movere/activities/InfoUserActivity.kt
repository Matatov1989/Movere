package com.matatov.movere.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.matatov.movere.R
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.ConstantsUtil.ARG_CONTACT_DATA
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class InfoUserActivity : AppCompatActivity(), OnMapReadyCallback {

    var userModel: UserModel? = null

    var textUserName: TextView? = null
    var textUserVehicle: TextView? = null
    var textUserTimestamp: TextView? = null

    var imageUser: ImageView? = null
    var imageUserVehicle: ImageView? = null

    var btnChat: Button? = null
    var btnWay: Button? = null

    var FORMATTER_TIME: DateFormat = SimpleDateFormat.getTimeInstance()
    var FORMATTER_DATE: DateFormat = SimpleDateFormat.getDateInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title

        textUserName = findViewById<TextView>(R.id.textUserName)
        textUserVehicle = findViewById<TextView>(R.id.textUserVehicle)
        textUserTimestamp = findViewById<TextView>(R.id.textUserTimestamp)

        imageUser = findViewById<ImageView>(R.id.imageUser)
        imageUserVehicle = findViewById<ImageView>(R.id.imageUserVehicle)

        btnChat = findViewById<Button>(R.id.btnChat)
        btnWay = findViewById<Button>(R.id.btnWay)

        userModel = intent.getParcelableExtra<Parcelable>(ARG_CONTACT_DATA) as UserModel

        initMap()
        initView()
    }

    //init map
    fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //init view
    fun initView(){
        textUserName!!.text = userModel!!.userName      //set user name
      //  textUserTimestamp!!.text = userModel!!.userTimeStamp.toString()     //set user timestamp

        val currentTime: Date = Calendar.getInstance().getTime()
        //Log.d(LOG_TAG, "*** contactSelected *** 1 " + FORMATTER_DATE.format(currentTime))
        //Log.d(LOG_TAG, ""*** contactSelected *** 2 " + FORMATTER_DATE.format(contactSelected.getUserTimeStamp().toDate())")
        if (FORMATTER_DATE.format(currentTime).equals(FORMATTER_DATE.format(userModel!!.userTimeStamp!!.toDate())))
            textUserTimestamp!!.text = FORMATTER_TIME.format(userModel!!.userTimeStamp!!.toDate())
        else
            textUserTimestamp!!.text = FORMATTER_DATE.format(userModel!!.userTimeStamp!!.toDate())

        val listVehicle = resources.getStringArray(R.array.arrTypeVehicle)

        //set icon and text to vehicle card
        when (userModel!!.userTypeVehicle) {
            -1 -> {
                textUserVehicle!!.text = listVehicle[4]
                imageUserVehicle!!.setImageResource(R.drawable.ic_baseline_help_outline_24)
            }
            0 -> {
                textUserVehicle!!.setText(getString(R.string.textTypeVehicle, listVehicle[0]))
                imageUserVehicle!!.setImageResource(R.drawable.ic_baseline_child_friendly_24)
            }
            1 -> {
                textUserVehicle!!.setText(getString(R.string.textTypeVehicle, listVehicle[1]))
                imageUserVehicle!!.setImageResource(R.drawable.ic_baseline_motorcycle_24)
            }
            2 -> {
                textUserVehicle!!.setText(getString(R.string.textTypeVehicle, listVehicle[2]))
                imageUserVehicle!!.setImageResource(R.drawable.ic_baseline_directions_car_24)
            }
            3 -> {
                textUserVehicle!!.setText(getString(R.string.textTypeVehicle, listVehicle[3]))
                imageUserVehicle!!.setImageResource(R.drawable.ic_baseline_airport_shuttle_24)
            }
        }

        //set user photo
        val requestOptions: RequestOptions = RequestOptions()
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)

        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(Uri.parse(userModel!!.userUriPhoto))
            .into(imageUser!!)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //   mMap = googleMap

        // Add a marker in Sydney and move the camera
        val position =
            LatLng(userModel!!.l!!.latitude, userModel!!.l!!.longitude)
        googleMap.addMarker(MarkerOptions().position(position))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 7.0f))
    }

    fun onClickChat(view: View) {
        startActivity(Intent(applicationContext, ChatActivity::class.java).putExtra(ARG_CONTACT_DATA, userModel))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun onClickWay(view: View) {
        val lat: String = java.lang.String.valueOf(userModel!!.l!!.latitude)
        val lon: String = java.lang.String.valueOf(userModel!!.l!!.longitude)
        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lon&mode=d&avoid=h")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}
