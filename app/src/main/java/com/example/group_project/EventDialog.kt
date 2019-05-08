package com.example.group_project

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import `in`.madapps.placesautocomplete.model.Place
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import android.widget.DatePicker
import android.widget.TextView
import android.util.Log
import android.view.View
import java.util.*
import kotlin.collections.ArrayList


class EventDialog: DialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var placesAutocomplete : AutoCompleteTextView? = null
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var mainActivity:MainActivity

    lateinit var dateTextView: TextView
    lateinit var timeTextView: TextView


    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    var hour: Int = 0
    var minute: Int = 0

    private var playerCounterTxt: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    private var playerCounter = 1



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{//, DatePickerDialog.OnDateSetListener {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.popup, null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            firebaseAuth = FirebaseAuth.getInstance()
            val userID = firebaseAuth.currentUser?.uid

            dateTextView = view.findViewById(R.id.add_event_date)
            timeTextView = view.findViewById(R.id.add_event_time)

            playerCounterTxt = view.findViewById(R.id.playerCounterTxt)

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

            dateTextView?.setOnClickListener {

                showDatePickerDialog()
            }

            timeTextView?.setOnClickListener {

                showTimePickerDialog()
            }


            view?.findViewById<Button>(R.id.plusBtn)?.setOnClickListener {

                playerCounter++
                playerCounterTxt!!.setText((playerCounter.toString()))
            }


            view?.findViewById<Button>(R.id.minusBtn)?.setOnClickListener {
                if (playerCounter != 1) {
                    playerCounter--
                    playerCounterTxt!!.setText((playerCounter.toString()))
                }
            }


            builder.setView(view)
                // Add action buttons
                .setMessage("Create a new Event")
                .setPositiveButton("Add Event") { dialog, it ->


                     //val time = view?.findViewById<EditText>(R.id.add_event_time)?.text.toString()


                    val sport = view?.findViewById<Spinner>(R.id.add_event_sport)?.selectedItem.toString()

                    val location = view?.findViewById<EditText>(R.id.add_event_location)?.text.toString()
                   // val players = view?.findViewById<EditText>(R.id.add_event_players_number)?.text.toString()

                    val ref = FirebaseDatabase.getInstance().getReference("events")
                    val host = user
                    val eventId = ref.push().key

                    var players = ArrayList<User>()

                    players.add(host!!)

                    val date = Date(year, month, day, hour, minute)

                    val event = Event(eventId, sport, date, location, playerCounter.toString(), host!!, players )

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

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

        this.year = year
        this.month = month
        this.day = day

        dateTextView.text = month.toString() + "/" + day.toString() + "/" + year.toString()

    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {

        this.hour = hour
        this.minute = minute

        timeTextView.text = hour.toString() + ":" + minute.toString() +  "    "

    }

    fun showDatePickerDialog() {

        val datePickerDialog = DatePickerDialog(
            context!!,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    fun showTimePickerDialog(){

        val timePickerDialog = TimePickerDialog(
            context!!,
            this,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            false)

        timePickerDialog.show()
    }

}

