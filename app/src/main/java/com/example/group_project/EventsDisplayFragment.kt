package com.example.group_project


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EventsDisplayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_events_display, container, false)

        val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

        model.getUserName().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.ownerTxt).text = value

        })


        model.getSport().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.sportTxt).text = value

        })


        model.getCalendar().observe(this, Observer<Calendar> { value ->

            v.findViewById<TextView>(R.id.timeTxt).text = value.time.toString()

        })

        model.getLocation().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.addressTxt).text = value

        })

        model.getDistance().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.distance).text = "Dstance:    " + value + " miles"

        })



        return v
    }


}
