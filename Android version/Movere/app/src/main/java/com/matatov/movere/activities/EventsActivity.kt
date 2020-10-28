package com.matatov.movere.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.matatov.movere.R
import com.matatov.movere.adapters.EventsRecyclerAdapter
import com.matatov.movere.interfaces.OnResultClickPosition
import com.matatov.movere.models.EventModel
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.FirestoreUtil
import kotlinx.android.synthetic.main.content_events.*


class EventsActivity : AppCompatActivity(), OnResultClickPosition.Event {

    var recyclerAdapter: EventsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userModel =
            intent!!.getParcelableExtra<Parcelable>(MapActivity::class.java.canonicalName) as UserModel

        // TODO : create class or dialog with creating event
        findViewById<FloatingActionButton>(R.id.fabCreateEvent).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        recyclerEvent!!.isClickable = true

        //get all events via radius of user 30 km
        FirestoreUtil.getEventsByRadiusLocalion(userModel!!.l, 30.0) { eventList ->

            recyclerAdapter = EventsRecyclerAdapter(this, eventList)

            recyclerEvent!!.adapter = recyclerAdapter
            recyclerEvent!!.layoutManager = LinearLayoutManager(this@EventsActivity)
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

    override fun onClickPosition(event: EventModel) {
        //TODO : think about this
    }
}
