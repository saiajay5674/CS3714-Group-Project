package com.example.group_project


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList



class EventsDisplayFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_events_display, container, false)

        val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

         recyclerView = v.findViewById(R.id.playerNameList)

        val adapter = DetailListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        model.getUserName().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.ownerTxt).text = value

        })


        model.getSport().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.sportTxt).text = value

        })


        model.getCalendar().observe(this, Observer<Date> { value ->

            v.findViewById<TextView>(R.id.timeTxt).text = value.toString()

        })

        model.getLocation().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.addressTxt).text = value

        })

        model.getDistance().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.distance).text = "Distance:    " + value + " miles"

        })

        model.getMaxPlayers().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.maxPlayersTxt).text = value

        })


        model.getPlayers().observe(this, Observer<ArrayList<User>> { value ->

            adapter.setPlayers(value)

        })


        return v
    }


    inner class DetailListAdapter(): RecyclerView.Adapter<DetailListAdapter.DetailUserViewHolder>() {

        var players = emptyList<User>()


        internal fun setPlayers(players: List<User>)
        {
            this.players = players
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailUserViewHolder {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.playerscard_view, parent, false)

            return DetailUserViewHolder(v)
        }

        override fun getItemCount(): Int {

            return players.size
        }

        override fun onBindViewHolder(holder: DetailUserViewHolder, position: Int) {

            holder.view.findViewById<TextView>(R.id.userNameTxt).text = players[position].username

        }

        inner class DetailUserViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {

                }
            }
        }
    }

}
