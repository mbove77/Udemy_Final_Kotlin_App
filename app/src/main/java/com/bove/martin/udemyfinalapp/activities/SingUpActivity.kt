package com.bove.martin.udemyfinalapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bove.martin.udemyfinalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.bove.martin.udemyfinalapp.utils.*
import kotlinx.android.synthetic.main.activity_sing_up.*


class SingUpActivity : AppCompatActivity() {
    // Initialize Firebase Auth
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        // Validate email in realtime
        editTextEmail.validate {
            editTextEmail.error = if (isValidateEmail(it)) null else "Email is invalid."
        }
        // Validate password in realtime
        editTextPassword.validate {
            editTextPassword.error = if (isValidatePassword(it)) null else "Password should contain 1 lowercase, 1 uppercase, 1 number, 1 special character and 4 characters length at least."
        }
        // Validate confirm password in realtime
        editTextConfirmPassword.validate {
            editTextConfirmPassword.error = if (isValidateConfirmPassword(editTextPassword.text.toString(), it)) null else "Password and the confirm password don´t match."
        }

        // Go back
        buttonBackToLogIn.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // SingUp Click
       buttonSingUp.setOnClickListener {
           val email = editTextEmail.text.toString()
           val password= editTextPassword.text.toString()
           val comfirPass = editTextConfirmPassword.text.toString()

           if(isValidateEmail(email) && isValidatePassword(password) && isValidateConfirmPassword(password, comfirPass)) {
                singUpByEmail(email, password)
            } else {
                toast("Please make sure all the data is correct.")
            }
       }
    }

    private fun singUpByEmail(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this)  {
                    toast("An email has ben sent to you. Please confirm before sing in")
                    goToActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }

            } else {
                // If sign in fails, display a message to the user.
                toast("createUserWithEmail:failure")
            }
        }
    }


}
