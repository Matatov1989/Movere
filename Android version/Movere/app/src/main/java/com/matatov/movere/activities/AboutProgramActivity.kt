package com.matatov.movere.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.matatov.movere.R

class AboutProgramActivity : AppCompatActivity() {

    var btnPrivacyPolicy: TextView? = null
    var btnWorkers: TextView? = null
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_program)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnPrivacyPolicy = findViewById<TextView>(R.id.btnPrivacyPolicy)
        btnWorkers = findViewById<TextView>(R.id.btnWorkers)

        btnPrivacyPolicy!!.setOnClickListener {
            btnPrivacyPolicy!!.setTextColor(resources.getColor(R.color.colorWhiteDark))

            startActivity(Intent(applicationContext, PrivacyPolicyActivity::class.java)
                .putExtra(AboutProgramActivity::class.java.canonicalName, true))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        btnWorkers!!.setOnClickListener {
            btnWorkers!!.setTextColor(resources.getColor(R.color.colorWhiteDark))
            dialogWorkers()
            btnWorkers!!.setTextColor(resources.getColor(R.color.colorBlack))
        }
    }

    private fun dialogWorkers() {
        val arrNames = resources.getStringArray(R.array.arrWorkers)
        val arrMails = resources.getStringArray(R.array.arrMails)
        val adb = AlertDialog.Builder(this)
        adb.setItems(arrNames) { dialog, item ->
            when (item) {
                0 -> sendMailForDev(arrMails[0])
                1 -> sendMailForDev(arrMails[1])
                2 -> sendMailForDev(arrMails[2])
            }
        }
        dialog = adb.show()
    }

    //send mail to select developer
    fun sendMailForDev(mailAddress: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAddress))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.toastSendMail)))
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
}
