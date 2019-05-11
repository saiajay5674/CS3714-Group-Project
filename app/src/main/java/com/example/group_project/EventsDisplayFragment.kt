package com.example.group_project


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList



class EventsDisplayFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var currentUser: User
    lateinit var joinButton: Button

    lateinit var event: Event
    lateinit var eventId: String
    lateinit var mainActivity: MainActivity

    private var mapFragment: MapFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    init {
        mapFragment = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_events_display, container, false)

        val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

         recyclerView = v.findViewById(R.id.playerNameList)

        val adapter = DetailListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        joinButton = v.findViewById(R.id.joinBtn)



        model.getEventId().observe(this, Observer<String> { value ->

            val ref = FirebaseDatabase.getInstance().getReference("events/" + value)

            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    //Do nothing
                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if(p0 != null)
                    {
                        if (p0!!.getValue(Event::class.java) != null)
                        {
                            event = p0?.getValue(Event::class.java)!!
                            eventId = event.event_id
                            v.findViewById<TextView>(R.id.ownerTxt).text = event.host.username
                            v.findViewById<TextView>(R.id.sportTxt).text = event.sport
                            v.findViewById<TextView>(R.id.timeTxt).text = event.date.toString()
                            v.findViewById<TextView>(R.id.addressTxt).text = event.location
                            v.findViewById<TextView>(R.id.maxPlayersTxt).text = event.maxPlayers

                            updatePlayers(v, adapter)
                            currentUser = model.getCurrentUser().value!!
                            setUpButton(v)
                        }

                    }

                }

            })


        })


        model.getDistance().observe(this, Observer<String> { value ->

            v.findViewById<TextView>(R.id.distance).text = "Distance:    " + value + " miles"


        })



        joinButton.setOnClickListener {

            if(!checkIfJoinned(currentUser, event.players))
            {
                var newList = event.players
                newList.add(currentUser)
                joinEvent(event, newList)
            }
            else {

                leaveEvent(event, currentUser)
            }

            setUpButton(v)
        }

        v.findViewById<ImageButton>(R.id.pin).setOnClickListener {

            model.setLocationChanged(true, event.location)

            openFragment(mapFragment)
        }

        return v
    }

    fun updatePlayers(view: View, adapter: DetailListAdapter)
    {
        val playerRef = FirebaseDatabase.getInstance().getReference("events/" + eventId + "/players")

        playerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //Do nothing
            }

            override fun onDataChange(p0: DataSnapshot?) {

                val newPlayers = ArrayList<User>()

                if(p0 != null)
                {
                    for(user in p0.children)
                    {
                        val player = user.getValue(User::class.java)
                        newPlayers.add(player!!)
                    }

                }

                view.findViewById<TextView>(R.id.joinedPlayersTitle).text = "Joined Players: " + event.players.size.toString()


                adapter.setPlayers(newPlayers)

            }

        })


    }


    fun setUpButton(view: View)
    {
        joinButton.isEnabled = !(event.maxPlayers.toInt() == event.players.size)

        if(currentUser.equals(event.host))
        {
            joinButton.visibility = View.GONE
        }
        else
        {
            joinButton.visibility = View.VISIBLE
        }


        if(event.players.contains(currentUser))
        {
            joinButton.isEnabled = true
            joinButton.text = "Leave"
        }
        else if (event.maxPlayers.toInt() == event.players.size)
        {
            joinButton.text = "Full"

            view.findViewById<TextView>(R.id.joinedPlayersTitle).text = "Joined Players: " + event.players.size.toString() + " (Full)"
        }
        else
        {
            joinButton.text = "Join"
        }
    }

    fun checkIfJoinned(user: User, playerList: ArrayList<User>): Boolean
    {
        return playerList.contains(user)
    }

    fun joinEvent(event: Event, playerList: ArrayList<User>)
    {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Joining Game...")
        progressDialog.setCanceledOnTouchOutside(false)

        val ref = FirebaseDatabase.getInstance().getReference("events")
        progressDialog.show()
        ref.child(event.event_id).child("players").setValue(playerList).addOnSuccessListener {

            val handler = Handler()
            handler.postDelayed( {
                progressDialog.dismiss()
                Toast.makeText(mainActivity, "You have joinned the event", Toast.LENGTH_SHORT).show()
            }, 1000)

        }
    }

    fun leaveEvent(event: Event, user: User)
    {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setMessage("Leaving Game...")
        val ref = FirebaseDatabase.getInstance().getReference("events")

        var newList = event.players

        newList.remove(user)

        progressDialog.show()

        ref.child(event.event_id).child("players").setValue(newList).addOnSuccessListener {

            val handler = Handler()
            handler.postDelayed( {
                progressDialog.dismiss()
                Toast.makeText(mainActivity, "You have been removed", Toast.LENGTH_SHORT).show()
            }, 1000)

        }
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


    private fun openFragment(fragment: Fragment) {
        val transaction = mainActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
