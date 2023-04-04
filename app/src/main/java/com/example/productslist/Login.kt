package com.example.productslist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()
    var currentUser = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val getAccount = findViewById<TextView>(R.id.getAccount)


        getAccount.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }


        val logIn = findViewById<Button>(R.id.logIn)
        logIn.setOnClickListener {
            logInBtn()
        }
    }

    fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
        }
        UID = AUTH.currentUser?.uid.toString()
    }

    private fun logInBtn() {
        val inputEmail: TextInputEditText = findViewById<TextInputEditText>(R.id.inputEmail)
        val inputPassword: TextInputEditText = findViewById<TextInputEditText>(R.id.inputPassword)

        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()


        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (!email.contains("@") || !email.matches(emailPattern.toRegex())) {
            showError(inputEmail, "Enter correct email")
        } else if (password.isEmpty()) {
            showError(inputPassword, "Enter password")
        } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = mAuth.currentUser
                        updateUI(currentUser)
                        finish()

                    } else {
                        showError(inputEmail, "This user doesn't exist")
                        showError(inputPassword, "This user doesn't exist")

                    }
                }

        }
    }

    fun showError(textInputEditText: TextInputEditText, error: String) {
        textInputEditText.error = error
        textInputEditText.requestFocus()
    }
}
