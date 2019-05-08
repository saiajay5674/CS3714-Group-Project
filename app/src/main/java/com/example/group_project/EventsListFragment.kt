package com.example.group_project



import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.location.*
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProviders
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.card_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class EventsListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var database: FirebaseDatabase
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mainActivity: MainActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    var latitude: Double? = null
    var longitude: Double? = null
    var currentLocation: Location? = null

    companion object {
        private const val PERMISSION_CODE: Int = 1000
        private const val UPDATE_INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 2000

    }

    val locationListener: LocationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLocationChanged(location: Location) {

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_events_list, container, false)

        //val model = activity.run { ViewModelProviders.of(this!!).get(EventViewModel::class.java) }

        recyclerView = view.findViewById(R.id.events_list)
        geocoder = Geocoder(context, Locale.getDefault())

        val recyclerView = view.findViewById<RecyclerView>(R.id.events_list)
        val adapter = EventListAdapter()
        database = FirebaseDatabase.getInstance()

        locationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(checkLocationPermission())
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, locationListener)
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequest
            .Builder(NotificationWorker::class.java, 1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueue(request)


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

    fun updateUserLocation()
    {

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

        fun getDistance(address: String): Float
        {
            val location = geocoder.getFromLocationName(address, 1)[0]

            //checkLocationPermission()
            if(currentLocation == null)
            {
                return -1f
            }

            val eventLocation = Location("Evnt Location")
            eventLocation.longitude = location.longitude
            eventLocation.latitude = location.latitude


            return currentLocation!!.distanceTo(eventLocation) / 1609.34f

        }

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

            val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")




            val dateFormat = SimpleDateFormat("hh:mm a yyyy-MM-dd")

            holder.view.findViewById<TextView>(R.id.date).text = dateFormat.format(events[position].date)
            holder.view.findViewById<TextView>(R.id.sport).text = events[position].sport
            holder.view.findViewById<TextView>(R.id.location).text = events[position].location
            holder.view.findViewById<TextView>(R.id.host).text = "Host " + events[position].host.username

            if (getDistance(events[position].location) < 1)
            {
                holder.view.findViewById<TextView>(R.id.distance).text = "Distance: < 1 mi"

            }
            else
            {
                holder.view.findViewById<TextView>(R.id.distance).text = "Distance " + getDistance(events[position].location).toString() + " mi"

            }


            holder.view.itemView.setOnClickListener {

                model.setvalue(events[position].date, events[position].sport, events[position].location, events[position].host.username, getDistance(events[position].location).toString())


                openFragment(EventsDisplayFragment())
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
            val popup = PopupMenu(context,holder.view.findViewById(R.id.options))

            holder.view.findViewById<ImageButton>(R.id.options).setOnClickListener {

                val popup = PopupMenu(context,holder.view.findViewById(R.id.options))
                val inflater = popup.menuInflater

                if (user!!.equals(events[position].host))
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

            popup.setOnMenuItemClickListener {

                when(it.title)
                {

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
    fun openFragment(fragment: Fragment) {
        val transaction = mainActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkLocationPermission(): Boolean {


        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    android.Manifest.permission.ACCESS_FINE_LOCATION))

                ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    , PERMISSION_CODE)

            else
                ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    , PERMISSION_CODE)

            return false
        } else
            return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)

                        if (checkLocationPermission()) {

                            return
                        }
                } else {
                    Toast.makeText(context!!, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}
