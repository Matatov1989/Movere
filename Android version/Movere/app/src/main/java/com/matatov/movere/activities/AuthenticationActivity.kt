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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.messaging.FirebaseMessaging
import com.matatov.movere.R
import com.matatov.movere.models.UserModel
import com.matatov.movere.utils.ConstantsUtil
import com.matatov.movere.utils.ConstantsUtil.LOG_TAG
import com.matatov.movere.utils.FireMessageUtil
import com.matatov.movere.utils.FirestoreUtil
import com.matatov.movere.view.ProgressDialogView
import com.valdesekamdem.library.mdtoast.MDToast

class AuthenticationActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var signInButton: SignInButton? = null
    private var btnPrivacyPolicy: TextView? = null
    private val progressDialog = ProgressDialogView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        // TODO : check why russian text align left
        val strDialog = getString(R.string.strProgressBar)
        progressDialog.show(this, strDialog)

        Log.d(LOG_TAG, "1 token "+ idToken)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val newUser = auth!!.currentUser

                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(LOG_TAG, "Fetching FCM registration token failed ", task.exception)

                            progressDialog.dialog!!.dismiss()
                            MDToast.makeText(
                                applicationContext,
                                getString(R.string.toastAuthError),
                                MDToast.LENGTH_LONG,
                                MDToast.TYPE_ERROR
                            ).show()
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        Log.d(LOG_TAG, "2 token "+ token)

                        val userModel = UserModel(
                            newUser!!.uid,
                            newUser!!.displayName,
                            newUser!!.photoUrl.toString(),
                            0,
                            token,
                            true,
                            "7zzzzzzzzz", //geoHash GeoPoint(0.0, 0.0)
                            GeoPoint(0.0, 0.0),
                            Timestamp.now()
                        )

                        FirestoreUtil.addNewUserToFirestore(userModel){ isSuccess ->
                            if (isSuccess) {
                                progressDialog.dialog!!.dismiss()

                                startActivity(Intent(applicationContext, MapActivity::class.java)
                                    .putExtra(AuthenticationActivity::class.java.canonicalName, userModel))
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                finish()

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
                    })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LOG_TAG, "signInWithCredential:failure", task.exception)
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
