package com.example.group_project

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var loginButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var signupText: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressDialog = ProgressDialog(this)
        loginButton = findViewById(R.id.login_button)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        signupText = findViewById(R.id.signup_text)
        firebaseAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener(this)
        signupText.setOnClickListener(this)

        if(firebaseAuth.currentUser != null)
        {
            finish()
            overridePendingTransition(0, 0)
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun userLogin()
    {
        val email = username.text.toString().trim()
        val pass = password.text.toString().trim()

        if(TextUtils.isEmpty(email))
        {
            this.username.setError("The field cannot be empty")
            return
        }
        if(TextUtils.isEmpty(pass))
        {
            this.password.setError("The field cannot be empty")
            return
        }

        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {

            progressDialog.dismiss()

            if (it.isSuccessful)
            {
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this, MainActivity::class.java))
            }
            else
            {
                this.password.setError("Incrorrect password")
            }
        }
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.login_button -> {
                userLogin()
            }
            R.id.signup_text -> {

                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this, SignupActivity::class.java))
            }
        }
    }
}
