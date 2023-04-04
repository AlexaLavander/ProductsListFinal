package com.example.productslist

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Register : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val register: Button = findViewById(R.id.register)
        val toLog = findViewById<TextView>(R.id.toLogin)
        UID = AUTH.currentUser?.uid.toString()

        register.setOnClickListener {
            Register()
        }

        toLog.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }


    fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            val intent = Intent(this@Register, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotConstructor")
    private fun Register() {
        val inputUsername: TextInputEditText = findViewById<TextInputEditText>(R.id.inputUsername)
        val inputEmail: TextInputEditText = findViewById<TextInputEditText>(R.id.inputEmail)
        val inputPassword: TextInputEditText = findViewById<TextInputEditText>(R.id.inputPassword)
        val confirmPassword: TextInputEditText = findViewById<TextInputEditText>(R.id.confirmPassword)

        val username = inputUsername.text.toString()
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val confirmPasswordText = confirmPassword.text.toString()

        val emailPattern: String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (username.isEmpty()) {
            showError(inputUsername, "Enter your username")
        } else if (!email.contains("@") || !email.matches(emailPattern.toRegex())) {
            showError(inputEmail, "Enter correct email")
        } else if (password.isEmpty()) {
            showError(inputPassword, "Enter password")
        } else if (password != confirmPasswordText) {
            showError(confirmPassword, "Passwords doesn't match")
        } else {
            databaseReferenceUsers.get().addOnSuccessListener {
                if (it.value.toString().contains(username)){
                    Toast.makeText(this, "User with this data already exists", Toast.LENGTH_LONG).show()
                } else{
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, OnCompleteListener   {
                            if (it.isSuccessful) {

                                val dateMap: MutableMap<String, Any> = LinkedHashMap()

                                dateMap["username"] = username
                                dateMap["email"] = email
                                dateMap["id"] = UID
                                val UID = AUTH.currentUser?.uid.toString()

                                databaseReferenceUsers.child(UID).updateChildren(dateMap)
                                val dateMapUsername: MutableMap<String, Any> = LinkedHashMap()
                                dateMapUsername["username"] = username
                                databaseReferenceUsernames.child(username).updateChildren(dateMapUsername)
                                val dateMapIds: MutableMap<String, Any> = LinkedHashMap()
                                dateMapIds["id"] = UID
                                databaseReferenceIds.child(username).updateChildren(dateMapIds)
                                val currentUser = mAuth.currentUser
                                updateUI(currentUser)
                                finish()
                            }
                        })
                }
            }
        }
    }

    fun showError(textInputEditText: TextInputEditText, error: String) {
        textInputEditText.error = error
        textInputEditText.requestFocus()
    }
}

