package com.example.group_project


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class UpdateEventFragment : Fragment(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    lateinit var dateTextView: TextView
    lateinit var timeTextView: TextView

    private var playerCounterTxt: EditText? = null

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    var hour: Int = 0
    var minute: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_update_event, container, false)


        val model = activity?.run { ViewModelProviders.of(this).get(ViewModel::class.java) }
            ?: throw Exception("Invalid Activity")


        view.findViewById<TextView>(R.id.edit_event_location).text = model.getEvent().value!!.location

      //  view.findViewById<EditText>(R.id.update_event_players_number).text = ""// model.getEvent().value!!.maxPlayers.toString()

        view.findViewById<TextView>(R.id.edit_event_time).text = model.getEvent().value!!.date.toString()

        val spinner =  view.findViewById<Spinner>(R.id.edit_event_sport)

        spinner.setSelection(getIndex(spinner, model.getEvent().value!!.sport))

        playerCounterTxt = view.findViewById(R.id.update_event_players_number)


        return view
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {

        var index = 0

        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i) == myString) {
                index = i
            }
        }
        return index
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

        this.year = year
        this.month = month
        this.day = day

        dateTextView.text = month.toString() + "/" + day.toString() + "/" + year.toString()

    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        var validTime = true

        var minHour =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var minMinute = Calendar.getInstance().get(Calendar.MINUTE)

        this.hour = hour
        this.minute = minute

        timeTextView.text = hour.toString() + ":" + minute.toString() + "    "




    }

    fun showDatePickerDialog() {

        val datePickerDialog = DatePickerDialog(
            context!!,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis

        Log.d("min date ++++++++++++", Calendar.getInstance().timeInMillis.toString() )

        datePickerDialog.show()
    }

    fun showTimePickerDialog() {

        val timePickerDialog = TimePickerDialog(
            context!!,
            this,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

}
