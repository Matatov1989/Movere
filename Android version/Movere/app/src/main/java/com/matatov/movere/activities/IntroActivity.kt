package com.matatov.movere.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.matatov.movere.R
import com.matatov.movere.adapters.IntroViewPagerAdapter
import com.matatov.movere.models.ScreenItemModel
import com.matatov.movere.utils.SharedPrefUtil
import com.matatov.movere.utils.SharedPrefUtil.IS_INTRO_OPENED
import java.util.ArrayList


class IntroActivity : AppCompatActivity() {

    private var screenPager: ViewPager? = null
    var introViewPagerAdapter: IntroViewPagerAdapter? = null

    var tabIndicator: TabLayout? = null
    var btnNext: Button? = null
    var btnGetStarted: Button? = null
    var tvSkip: TextView? = null

    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_intro)

        // ini views
        btnNext = findViewById<Button>(R.id.btn_next)
        btnGetStarted = findViewById<Button>(R.id.btn_get_started)
        tabIndicator = findViewById<TabLayout>(R.id.tab_indicator)
        tvSkip = findViewById<TextView>(R.id.tv_skip)

        // fill list screen
        val mList: MutableList<ScreenItemModel> = ArrayList<ScreenItemModel>()
        mList.add(
            ScreenItemModel(
                getString(R.string.screenTitle1),
                getString(R.string.screenDesc1),
                R.drawable.ic_intro_welcome
            )
        )
        mList.add(
            ScreenItemModel(
                getString(R.string.screenTitle2),
                getString(R.string.screenDesc2),
                R.drawable.ic_intro_smiley
            )
        )

        // setup viewpager
        screenPager = findViewById<ViewPager>(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager!!.setAdapter(introViewPagerAdapter)

        // setup tab layout with viewpager
        tabIndicator!!.setupWithViewPager(screenPager)

        // next button click Listner
        btnNext!!.setOnClickListener(View.OnClickListener {
            position = screenPager!!.currentItem
            if (position < mList.size) {
                position++
                screenPager!!.currentItem = position
            }
            if (position == mList.size - 1) { // when we rech to the last screen
                loadLastScreen()
            }
        })

        // tab layout add change listener
        tabIndicator!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab!!.position == mList.size - 1) loadLastScreen()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab!!.position == mList.size - 1) loadBackScreen()
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        // Get Started button click listener
        btnGetStarted!!.setOnClickListener(View.OnClickListener { //open CalendarActivity
            startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            SharedPrefUtil.putBooleanPref(IS_INTRO_OPENED, true, applicationContext)
            finish()
        })

        // skip button click listener
        tvSkip!!.setOnClickListener(View.OnClickListener { screenPager!!.currentItem = mList.size })
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private fun loadLastScreen() {
        btnNext!!.visibility = View.INVISIBLE
        btnNext!!.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        tvSkip!!.visibility = View.INVISIBLE
        tvSkip!!.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        tabIndicator!!.visibility = View.INVISIBLE
        tabIndicator!!.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        btnGetStarted!!.visibility = View.VISIBLE
        btnGetStarted!!.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
    }

    private fun loadBackScreen() {
        btnNext!!.visibility = View.VISIBLE
        btnNext!!.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        tvSkip!!.visibility = View.VISIBLE
        tvSkip!!.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        tabIndicator!!.visibility = View.VISIBLE
        tabIndicator!!.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        btnGetStarted!!.visibility = View.INVISIBLE
        btnGetStarted!!.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
    }
}
