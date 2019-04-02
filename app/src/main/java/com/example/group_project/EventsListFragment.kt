package com.example.group_project


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider


/**
 * A simple [Fragment] subclass.
 *
 */
class EventsListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_events_list, container, false)

        //val model = activity.run { ViewModelProviders.of(this!!).get(EventViewModel::class.java) }

        recyclerView = view.findViewById(R.id.events_list)

        val recyclerView = view.findViewById<RecyclerView>(R.id.events_list)
        val adapter = EventListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    inner class EventListAdapter():
        RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

        var events = emptyList<Event>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)

            return EventViewHolder(v)
        }

        override fun getItemCount(): Int {

            return events.size
        }

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        }

        inner class EventViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {

                }
            }
        }
    }


}
