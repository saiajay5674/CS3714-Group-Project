package com.example.group_project

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class
MainActivity : AppCompatActivity(){

    private var mBottomNav: BottomNavigationView? = null

    private var eventsListFragment: EventsListFragment
    private var mapFragment: MapFragment
    private var profileFragment: ProfileFragment

    init {

        eventsListFragment = EventsListFragment()
        mapFragment = MapFragment()
        profileFragment = ProfileFragment()
    }

    private var selectedNavigation: Int? = null
    private val listener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when(item.itemId) {

            R.id.menu_new_event -> {
                if(selectedNavigation == R.id.menu_new_event)
                {
                    return@OnNavigationItemSelectedListener true
                }
                selectedNavigation = R.id.menu_new_event
                openFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_events -> {
                if(selectedNavigation == R.id.menu_events)
                {
                    return@OnNavigationItemSelectedListener true
                }
                selectedNavigation = R.id.menu_events
                openFragment(eventsListFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_profile -> {
                if(selectedNavigation == R.id.menu_profile)
                {
                    return@OnNavigationItemSelectedListener true
                }
                selectedNavigation = R.id.menu_profile
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