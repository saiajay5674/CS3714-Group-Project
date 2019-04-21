package com.example.group_project

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern
import org.apache.commons.validator.routines.EmailValidator;


class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var signupButton: Button
    private lateinit var emailAddress: EditText
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var loginText: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        progressDialog = ProgressDialog(this)
        signupButton = findViewById(R.id.signup_button)
        emailAddress = findViewById(R.id.signup_email)
        password = findViewById(R.id.signup_password)
        username = findViewById(R.id.signup_username)
        phoneNumber = findViewById(R.id.signup_phone)
        loginText = findViewById(R.id.login_text)
        firebaseAuth = FirebaseAuth.getInstance()

        signupButton.setOnClickListener(this)
        loginText.setOnClickListener(this)
    }

    private fun registerUser()
    {
        if(!validateInput())
        {
            return
        }

        val email = emailAddress.text.toString().trim()
        val pass = password.text.toString().trim()
        val username = username.text.toString().trim()
        val phone = phoneNumber.text.toString().trim()

        //Registering user
        progressDialog.setMessage("Registering User...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {

            progressDialog.dismiss()

            if (it.isSuccessful) {

                val ref = FirebaseDatabase.getInstance().getReference("users")

                val uid = it.result.user.uid
                val user = User(uid, username, email, phone)
                Log.d("UID-------------->", uid.toString())
                ref.child(uid).setValue(user).addOnCompleteListener {
                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                }


                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
            else {
                Log.d("Create Sign In ------------>", it.exception.toString())
                Toast.makeText(this, "Could not register Try again", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateInput(): Boolean
    {
        val email = emailAddress.text.toString().trim()
        val pass = password.text.toString().trim()
        val username = username.text.toString().trim()
        val phone = phoneNumber.text.toString().trim()

        var register = true
        if(TextUtils.isEmpty(email))
        {
            emailAddress.setError("The field cannot be blank")
            register = false
        }
        if(TextUtils.isEmpty(pass))
        {
            password.setError("The field cannot be blank")
            register = false
        }
        if(!isValidEmail(email))
        {
            emailAddress.setError("Please enter a valid email address")
            register = false
        }
        if(TextUtils.isEmpty(username))
        {
            this.username.setError("The field cannot be blank")
            register = false
        }
        if(TextUtils.isEmpty(phone))
        {
            phoneNumber.setError("The field cannot be blank")
            register = false
        }


        return register
    }

    private fun isValidEmail(email: String): Boolean
    {
        val validator = EmailValidator.getInstance()

        if (!validator.isValid(email)) {

            return false
        }

        return true
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.signup_button -> {
                registerUser()
            }

            R.id.login_text -> {
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }


}
