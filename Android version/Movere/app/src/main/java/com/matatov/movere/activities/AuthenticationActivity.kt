package com.matatov.movere.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.matatov.movere.R
import com.matatov.movere.utils.FirestoreUtil
import com.matatov.movere.view.ProgressDialogView
import com.valdesekamdem.library.mdtoast.MDToast

class AuthenticationActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var signInButton: SignInButton? = null
    private var btnPrivacyPolicy: TextView? = null
    private val progressDialog = ProgressDialogView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton = findViewById<SignInButton>(R.id.auth_button)
        btnPrivacyPolicy = findViewById<TextView>(R.id.btnPrivacyPolicy)

        signInButton!!.setOnClickListener { signIn() }

        btnPrivacyPolicy!!.setOnClickListener {
            btnPrivacyPolicy!!.setTextColor(resources.getColor(R.color.colorWhiteDark))

            startActivity(Intent(applicationContext, PrivacyPolicyActivity::class.java)
                .putExtra(AuthenticationActivity::class.java.canonicalName, true))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                //     Log.d(LOG_TAG, "onActivityResult ApiException " + e.message)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        // TODO : check why text align left
        val strDialog = getString(R.string.strProgressBar)
        progressDialog.show(this, strDialog)

        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser

                    //add new user to Firestore cloud
                    FirestoreUtil.addNewUserToFirestore(user){ isSuccess ->
                        if (isSuccess) {
                            progressDialog.dialog!!.dismiss()
                            startActivity(Intent(applicationContext, MapActivity::class.java))
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        } else {
                            progressDialog.dialog!!.dismiss()
                            MDToast.makeText(
                                applicationContext,
                                getString(R.string.toastAuthError),
                                MDToast.LENGTH_LONG,
                                MDToast.TYPE_ERROR
                            ).show()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dialog!!.dismiss()
                    MDToast.makeText(
                        applicationContext,
                        getString(R.string.toastAuthError),
                        MDToast.LENGTH_LONG,
                        MDToast.TYPE_ERROR
                    ).show()
                }
            }
    }
}
