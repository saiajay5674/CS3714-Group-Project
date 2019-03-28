package com.example.group_project

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class Dialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.popup, null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setMessage("Create a new Event")
                .setPositiveButton("Add Event") { dialog, it ->


                    val sport = view?.findViewById<Spinner>(R.id.add_event_sport)?.selectedItem.toString()
                    val time = view?.findViewById<EditText>(R.id.add_event_time)?.text.toString()
                    val location = view?.findViewById<EditText>(R.id.add_event_location)?.text.toString()
                    val players = view?.findViewById<EditText>(R.id.add_event_players_number)?.text.toString()


                    val ref = FirebaseDatabase.getInstance().getReference("events")

                    val eventId = ref.push().key

                    val event = Event(eventId, sport, time, location, players)

                    ref.child(eventId).setValue(event).addOnCompleteListener {

                        //Toast.makeText(activity, "Your event has been added", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("cancel") { dialog, id ->
                    getDialog()?.cancel()
                }
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
    }

