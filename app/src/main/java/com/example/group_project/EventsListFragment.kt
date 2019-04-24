package com.example.group_project


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.card_view.view.*
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 *
 */
class EventsListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var database: FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_events_list, container, false)

        //val model = activity.run { ViewModelProviders.of(this!!).get(EventViewModel::class.java) }

        recyclerView = view.findViewById(R.id.events_list)


        val recyclerView = view.findViewById<RecyclerView>(R.id.events_list)
        val adapter = EventListAdapter()
        database = FirebaseDatabase.getInstance()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val ref = database.getReference("events")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {

                val events = ArrayList<Event>()

                if (p0!!.exists())
                {
                    for(event in p0.children)
                    {
                        val event = event.getValue(Event::class.java)
                        events.add(event!!)
                    }
                }

                adapter.setEvents(events)
            }

        })




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


            holder.view.findViewById<TextView>(R.id.date).text = events[position].time
            holder.view.findViewById<TextView>(R.id.sport).text = events[position].sport
            holder.view.findViewById<TextView>(R.id.location).text = events[position].location
            holder.view.findViewById<TextView>(R.id.host).text = events[position].host.username
            holder.view.itemView.setOnClickListener {

            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid

            val ref = FirebaseDatabase.getInstance().getReference("users/" + uid)

            var user: User? = null

            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    //Does nothing
                }

                override fun onDataChange(p0: DataSnapshot?) {

                    user = p0?.getValue(User::class.java)

                }

            })  // This gets the user object of the current User

            holder.view.findViewById<ImageButton>(R.id.options).setOnClickListener {

                val popup = PopupMenu(context,holder.view.findViewById(R.id.options))
                val inflater = popup.menuInflater

                if (user!!.uid == events[position].host.uid)
                {
                    inflater.inflate(R.menu.actions_creator, popup.menu)
                    popup.show()
                }
                else
                {
                    inflater.inflate(R.menu.actions_public, popup.menu)
                    popup.show()
                }

            }
        }


        internal fun setEvents(events: List<Event>)
        {
            this.events = events
            notifyDataSetChanged()
        }

        inner class EventViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {

                }
            }
        }
    }


}
