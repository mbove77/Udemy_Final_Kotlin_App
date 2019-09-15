package com.bove.martin.udemyfinalapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bove.martin.udemyfinalapp.MainActivity
import com.bove.martin.udemyfinalapp.R
import com.bove.martin.udemyfinalapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val mAuth by lazy { FirebaseAuth.getInstance() }
    private var currentUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        currentUser = mAuth.currentUser

        // If user is already login sent to main activity
        if(currentUser !== null && currentUser!!.isEmailVerified) { goToActivity<MainActivity>() }

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

        // Sing Up Activity
        buttonSingIn.setOnClickListener { gotoSingUpActivity() }

        // Forgot pass click
        textViewForgotPassword.setOnClickListener { gotoForgotPassActivity() }
    }

    fun checkloginValues(email: String, pass: String) :Boolean {
        return isValidateEmail(email) && isValidatePassword(pass)
    }

    fun singInWithEmailAndPassword(email: String, password: String) {
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

    fun gotoSingUpActivity() {
        val i = Intent(this, SingUpActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun gotoForgotPassActivity() {
        val i = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
