package com.bove.martin.udemyfinalapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bove.martin.udemyfinalapp.R
import com.bove.martin.udemyfinalapp.utils.goToActivity
import com.bove.martin.udemyfinalapp.utils.isValidateEmail
import com.bove.martin.udemyfinalapp.utils.toast
import com.bove.martin.udemyfinalapp.utils.validate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*


class ForgotPasswordActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editTextEmail.validate {
            editTextEmail.error = if (isValidateEmail(it)) null else "Email is invalid."
        }

        buttonForot.setOnClickListener {
            val email = editTextEmail.text.toString()
            if(isValidateEmail(email)) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    toast("An email has ben send to reset your passwords.", duracion = Toast.LENGTH_LONG)
                    goToActivity<LoginActivity>( ) {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }
            } else {
                toast("Please make sure the email address is correct.")
            }
        }

        // Go back
        buttonBackToLogIn.setOnClickListener {
            goToActivity<LoginActivity>() {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}
