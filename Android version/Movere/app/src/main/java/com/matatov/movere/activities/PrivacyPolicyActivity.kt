package com.matatov.movere.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.matatov.movere.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        webView!!.loadUrl("https://sites.google.com/view/movere-privacy-policy/")
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (intent.getBooleanExtra(AuthenticationActivity::class.java.canonicalName, false)){
            startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }else if (intent.getBooleanExtra(AboutProgramActivity::class.java.canonicalName, false)){
            startActivity(Intent(applicationContext, AboutProgramActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
