package com.example.group_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    //private lateinit var fragment: Fragment
    //private lateinit var mMap: GoogleMap
    private var mBottomNav: BottomNavigationView? = null

    private val listener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId) {

            R.id.menu_new_event -> {
                val mapFragment = MapFragment.newInstance()
                openFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_events -> {
                val eventsListfragment = EventsListFragment()
                openFragment(eventsListfragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_profile -> {
                val profileFragment = ProfileFragment()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        mBottomNav = findViewById(R.id.bottom_navigation)
        mBottomNav?.setOnNavigationItemSelectedListener(listener)



    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}