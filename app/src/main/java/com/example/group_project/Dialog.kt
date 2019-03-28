package com.example.group_project

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class Dialog: DialogFragment() {


    private val addEvent = DialogInterface.OnClickListener { dialog, id ->

        val sport = view?.findViewById<Spinner>(R.id.add_event_sport)?.selectedItem.toString()
        val time = view?.findViewById<EditText>(R.id.add_event_time)?.text.toString()
        val location = view?.findViewById<EditText>(R.id.add_event_location)?.text.toString()
        val players = view?.findViewById<EditText>(R.id.add_event_players_number)?.text.toString()


        val ref = (activity as MainActivity).database.getReference("events")

        val event_id = ref.push().key

        var event: Event = Event(event_id, sport, time, location, players)

        ref.child(event_id).setValue(event).addOnCompleteListener {

            Toast.makeText(activity?.applicationContext, "Your event has been added", Toast.LENGTH_LONG).show()
        }

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.popup, null))
                // Add action buttons
                .setMessage("Create a new Event")
                .setPositiveButton("Add Event",addEvent)
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    }

