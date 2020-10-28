package com.matatov.movere.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.matatov.movere.R
import com.matatov.movere.utils.FirestoreUtil
import com.matatov.movere.utils.SharedPrefUtil
import com.matatov.movere.utils.SharedPrefUtil.IS_INTRO_OPENED


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // TODO : think about get data from Firestore and after open a MapsActivity
        Handler().postDelayed(object : Runnable {
            override fun run() {

                // when this activity is about to be launch we need to check if its openened before or not
                if (SharedPrefUtil.getBooleanPref(IS_INTRO_OPENED, false, applicationContext)) {
                    FirestoreUtil.getCurrentUser(FirebaseAuth.getInstance().currentUser?.uid.toString()){ user ->
                        startActivity(Intent(applicationContext, MapActivity::class.java)
                            .putExtra(SplashActivity::class.java.canonicalName, user))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                } else {
                    startActivity(Intent(applicationContext, IntroActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }
        }, 2000)
    }
}
