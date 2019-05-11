package com.example.group_project


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity



class NoInternetFragment : AppCompatActivity(), View.OnTouchListener {

    private var fragment: Int? = null

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if(!isNetworkAvailable())
        {
            return true
        }

        when(fragment)
        {
            0 -> {
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this, LoginActivity::class.java))
            }

            1 -> {
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this, SignupActivity::class.java))

            }
            2 -> {
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)



        val bundle = intent.extras
        fragment = bundle.getInt("open")

        val view = findViewById<View>(R.id.no_internet_view)

        view.setOnTouchListener(this)

    }

    private fun isNetworkAvailable(): Boolean
    {
        try {

            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
        }
        catch (e: java.lang.Exception)
        {
            return false
        }
    }


}
