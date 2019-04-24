package com.example.group_project

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import `in`.madapps.placesautocomplete.model.Place
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView
import kotlinx.android.synthetic.main.activity_login.*


class EventDialog: DialogFragment() {

    private var placesAutocomplete : AutoCompleteTextView? = null
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var mainActivity:MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.popup, null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            firebaseAuth = FirebaseAuth.getInstance()
            val userID = firebaseAuth.currentUser?.uid

            val placesApi = PlaceAPI.Builder().apiKey("AIzaSyAeMzjhWrb6ZCLmhkqzelmY6LD63e2_VPY").build(mainActivity)

            placesAutocomplete = view?.findViewById(R.id.add_event_location)
            placesAutocomplete?.setAdapter(PlacesAutoCompleteAdapter(activity!!.applicationContext, placesApi))

            placesAutocomplete?.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    val place = parent.getItemAtPosition(position) as Place
                    placesAutocomplete?.setText(place.description)
                }

            val ref = FirebaseDatabase.getInstance().getReference("users/" + userID)

            var user: User? = null

            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    //Does nothing
                }

                override fun onDataChange(p0: DataSnapshot?) {

                    user = p0?.getValue(User::class.java)

                }

            })

            builder.setView(view)
                // Add action buttons
                .setMessage("Create a new Event")
                .setPositiveButton("Add Event") { dialog, it ->


                    val sport = view?.findViewById<Spinner>(R.id.add_event_sport)?.selectedItem.toString()
                    val time = view?.findViewById<EditText>(R.id.add_event_time)?.text.toString()
                    val location = view?.findViewById<EditText>(R.id.add_event_location)?.text.toString()
                    val players = view?.findViewById<EditText>(R.id.add_event_players_number)?.text.toString()

                    val ref = FirebaseDatabase.getInstance().getReference("events")
                    val host = user
                    val eventId = ref.push().key

                    val event = Event(eventId, sport, time, location, players, host!! )

                    ref.child(eventId).setValue(event).addOnCompleteListener {

                        Toast.makeText(mainActivity, "Event created", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("cancel") { dialog, id ->
                    getDialog()?.cancel()
                }
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }


}

