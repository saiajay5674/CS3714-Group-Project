package com.example.group_project

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment

class Dialog:DialogFragment() {

    private val addEvent = DialogInterface.OnClickListener { dialog, id ->

        val sport = view?.findViewById<Spinner>(R.id.games_spinner)?.selectedItem.toString()
        val time = view?.findViewById<EditText>(R.id.chooseTime)?.text.toString()
        val location = view?.findViewById<EditText>(R.id.chooseTime)?.text.toString()


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
                .setPositiveButton("Add Event",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    }

