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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class UpdateEventFragment : Fragment(),  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    lateinit var dateTextView: TextView
    lateinit var timeTextView: TextView

    private var editEventButton: Button? = null
    private var editCancelButton: Button? = null

    private var playerCounterTxt: EditText? = null

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    var hour: Int = 0
    var minute: Int = 0

    private var playerCounter = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_update_event, container, false)


        val model = activity?.run { ViewModelProviders.of(this).get(ViewModel::class.java) }
            ?: throw Exception("Invalid Activity")

        editEventButton = view.findViewById(R.id.updateEditbtn)
        editCancelButton = view.findViewById(R.id.updateCancelBtn)

        dateTextView = view.findViewById(R.id.edit_event_date)
        timeTextView = view.findViewById(R.id.edit_event_time)

        view.findViewById<TextView>(R.id.edit_event_location).text = model.getEvent().value!!.location

        val dateFormatDate = SimpleDateFormat("MM-dd-yyyy")
        val dateFormatTime = SimpleDateFormat("hh:mm")


        view.findViewById<TextView>(R.id.edit_event_date).text = dateFormatDate.format((model.getEvent().value!!.date))//dateFormat.format.getEvent().value!!.date
        view.findViewById<TextView>(R.id.edit_event_time).text = dateFormatTime.format((model.getEvent().value!!.date))

        val spinner =  view.findViewById<Spinner>(R.id.edit_event_sport)

        spinner.setSelection(getIndex(spinner, model.getEvent().value!!.sport))

        playerCounterTxt = view.findViewById(R.id.update_event_players_number)

        playerCounter = model.getEvent().value!!.maxPlayers.toInt()

        playerCounterTxt!!.setText(model.getEvent().value!!.maxPlayers)

        view?.findViewById<Button>(R.id.updatePlusBtn)?.setOnClickListener {

            playerCounter++
            playerCounterTxt!!.setText((playerCounter.toString()))
        }


        view?.findViewById<Button>(R.id.updateMinusBtn)?.setOnClickListener {
            if (playerCounter != 1) {
                playerCounter--
                playerCounterTxt!!.setText((playerCounter.toString()))
            }
        }


        playerCounterTxt?.addTextChangedListener {
            playerCounter = it.toString().toInt()

        }

        dateTextView?.setOnClickListener {

            showDatePickerDialog()
        }

        timeTextView?.setOnClickListener {

            showTimePickerDialog()
        }


        editEventButton?.setOnClickListener {

            var addEvent = true


            val sport = view?.findViewById<Spinner>(R.id.edit_event_sport)?.selectedItem.toString()

            val location = view?.findViewById<EditText>(R.id.edit_event_location)?.text.toString()



            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, hour, minute, 0)


            if (location.isEmpty()) {
                view?.findViewById<EditText>(R.id.edit_event_location)?.setError("Please choose location")

                addEvent = false

            }

            if (dateTextView.text.length == 0)
            {
                dateTextView.setError("Invalid Date")
                addEvent = false
            }

            if (timeTextView.text.length == 0)
            {
                timeTextView.setError("Invalid Time")
                addEvent = false
            }
            if (addEvent) {

                var minHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                var minMinute = Calendar.getInstance().get(Calendar.MINUTE)


                if ((day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) && (hour < minHour || (hour === minHour && minute < minMinute))) {

                    timeTextView.setError("Invalid Time")

                } else {

                }

            }
        }


        editCancelButton?.setOnClickListener {

           // dismiss()
        }


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

        dateTextView.text = month.toString() + "-" + day.toString() + "-" + year.toString()

    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {

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
