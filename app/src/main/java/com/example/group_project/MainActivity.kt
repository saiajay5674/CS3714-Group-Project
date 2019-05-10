package com.example.group_project

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class
MainActivity : AppCompatActivity(){

    private var mBottomNav: BottomNavigationView? = null

    private lateinit var progressDialog: ProgressDialog
    private var eventsListFragment: EventsListFragment
    private var mapFragment: MapFragment
    private var profileFragment: ProfileFragment
    lateinit var currentUser: User

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

        val model = run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

        progressDialog = ProgressDialog(this)

        if (FirebaseAuth.getInstance().currentUser != null)
        {
            progressDialog.setMessage("Loading....")
            progressDialog.show()
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            val userRef = FirebaseDatabase.getInstance().getReference("users/" + uid)


            userRef.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    //Does nothing
                }
                override fun onDataChange(p0: DataSnapshot?) {

                    if (p0 != null)
                    {
                        currentUser = p0?.getValue(User::class.java)!!
                        model.setCurrentUser(currentUser)
                        mBottomNav?.setOnNavigationItemSelectedListener(listener)
                        progressDialog.dismiss()
                    }
                }
            })  // This gets the user object of the current User
        }
        else {
            finish()
            overridePendingTransition(0, 0)
            startActivity(Intent(this, LoginActivity::class.java))
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mBottomNav = findViewById(R.id.bottom_navigation)

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}