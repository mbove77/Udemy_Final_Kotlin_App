package com.bove.martin.udemyfinalapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bove.martin.udemyfinalapp.MainActivity
import com.bove.martin.udemyfinalapp.R
import com.bove.martin.udemyfinalapp.utils.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), OnConnectionFailedListener {
    private val RC_GOOGLE_SG = 77
    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private val mGoogleApiClient: GoogleApiClient by lazy { getGoogleApiClient() }
    private var currentUser : FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        currentUser = mAuth.currentUser

        /*  If user is already login sent to main activity
        if(currentUser !== null && currentUser!!.isEmailVerified) { goToActivity<MainActivity>() }
         */

        // Realtime validation
        editTextEmail.validate {
            editTextEmail.error = if (isValidateEmail(it)) null else "Email is invalid."
        }
        // Validate password in realtime
        editTextPassword.validate {
            editTextPassword.error = if (isValidatePassword(it)) null else "Password should contain 1 lowercase, 1 uppercase, 1 number, 1 special character and 4 characters length at least."
        }

        // SingUp Button
        buttonSingUp.setOnClickListener {
            val email = editTextEmail.text.toString()
            val pass = editTextPassword.text.toString()

            if(checkloginValues(email, pass)) {
                singInWithEmailAndPassword(email, pass)
            } else {
                toast("Please make sure all the data is correct.")
            }
        }

        // Google sing in
        buttonLoginGoogle.setOnClickListener {
            val sigIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(sigIntent, RC_GOOGLE_SG)
        }

        // Sing Up Activity
        buttonSingIn.setOnClickListener { gotoSingUpActivity() }

        // Forgot pass click
        textViewForgotPassword.setOnClickListener { gotoForgotPassActivity() }
    }

    private fun checkloginValues(email: String, pass: String) :Boolean {
        return isValidateEmail(email) && isValidatePassword(pass)
    }

    private fun singInWithEmailAndPassword(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    currentUser = mAuth.currentUser

                    if(currentUser!!.isEmailVerified) {
                       goToActivity<LoginActivity> {
                           flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                       }
                    } else {
                        toast("Please verify your email before login.")
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun gotoSingUpActivity() {
        val i = Intent(this, SingUpActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun gotoForgotPassActivity() {
        val i = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Construimos el GoogleApiCliet
    private fun getGoogleApiClient(): GoogleApiClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    // Manejamos el resultado del intent del google sign in
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_GOOGLE_SG) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess) {
                loginFromGoogleToFirebase(result.signInAccount!!)
            }
        }
    }

    // Logeamos el usuario en la app
    private fun loginFromGoogleToFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)

        // Iniciamos sección
        mAuth.signInWithCredential(credentials).addOnCompleteListener {
            // Cerramos la sección de google para que el usuario pueda escoger otra cuenta en el futuro si lo desea
            if (mGoogleApiClient.isConnected) Auth.GoogleSignInApi.signOut(mGoogleApiClient)

            goToActivity<MainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    // Maneja eventos de conexión con Firebase
    override fun onConnectionFailed(result: ConnectionResult) {
        toast("Connection with Firebase fail.")
    }
}
