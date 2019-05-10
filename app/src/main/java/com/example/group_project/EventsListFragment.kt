package com.example.group_project



import android.content.Context
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.card_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventsListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var database: FirebaseDatabase
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mainActivity: MainActivity
    lateinit var currentUser: User


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    var currentLocation: Location? = null

    companion object {
        private const val PERMISSION_CODE: Int = 1000
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

        val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")


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
        recyclerView.layoutManager = LinearLayoutManager(context).apply { isAutoMeasureEnabled = false }

//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val request = PeriodicWorkRequest
//            .Builder(NotificationWorker::class.java, 1, TimeUnit.MINUTES)
//            .setConstraints(constraints)
//            .build()
//
//        WorkManager.getInstance().enqueue(request)



        currentUser = model.getCurrentUser().value!!

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

        fun getDistance(address: String): Float?
        {
            var location: Address?

            try {
                location = geocoder.getFromLocationName(address, 1)[0]
            }catch (e : java.lang.Exception) {
                location = null
            }

            //checkLocationPermission()
            if(currentLocation == null)
            {
                return -1f
            }

            val eventLocation = Location("Evnt Location")

            if(location == null)
            {
                return null
            }
            eventLocation.longitude = location.longitude
            eventLocation.latitude = location.latitude


            return currentLocation!!.distanceTo(eventLocation) / 1609.34f

        }

        fun setImage(view: View, sport: String)
        {
            when(sport)
            {
                "Basketball" -> {
                    Glide.with(this@EventsListFragment)
                        .load("http://www.amisvegetarian.com/wp-content/uploads/2018/10/basketball-clipart-free-basketball-clipart-clipart-panda-free-clipart-images-history-clipart-1024x1024.png")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById<ImageView>(R.id.event_image))
                }
                "Tennis" -> {
                    Glide.with(this@EventsListFragment)
                        .load("http://clipart-library.com/images/8c686Xr9i.jpg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById<ImageView>(R.id.event_image))
                }
                "Badminton" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://images6.alphacoders.com/439/thumb-1920-439763.jpg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Volleyball" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://3er1viui9wo30pkxh1v2nh4w-wpengine.netdna-ssl.com/wp-content/uploads/prod/sites/93/2018/05/FIVB-vball-1024x683.png")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Soccer" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://wallpapercave.com/wp/qHXu91n.jpg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Football" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://www.google.com/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiM_9WR24_iAhUNnOAKHdhDAiEQjRx6BAgBEAU&url=https%3A%2F%2Fatgbcentral.com%2Famerican-football-wallpaper.html&psig=AOvVaw0ctpfMtPnNJVwGhw70G-b7&ust=1557534718745555")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Golf" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://images.designtrends.com/wp-content/uploads/2016/02/28045208/feature93.jpg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
            }
        }

        fun joinEvent(event: Event, playerList: ArrayList<User>)
        {
            val ref = FirebaseDatabase.getInstance().getReference("events")

            ref.child(event.event_id).child("players").setValue(playerList).addOnSuccessListener {

                Toast.makeText(mainActivity, "You have joinned the event", Toast.LENGTH_SHORT).show()
            }
        }

        fun leaveEvent(event: Event, user: User)
        {
            val ref = FirebaseDatabase.getInstance().getReference("events")

            var newList = event.players

            newList.remove(user)

            ref.child(event.event_id).child("players").setValue(newList).addOnSuccessListener {

                Toast.makeText(mainActivity, "You have been removed", Toast.LENGTH_SHORT).show()
            }
        }

        fun setUpButton(view: View, user: User, event: Event)
        {
            view.findViewById<Button>(R.id.join_button).isEnabled = !(event.maxPlayers.toInt() == event.players.size)

            if(user.equals(event.host))
            {
                view.findViewById<Button>(R.id.join_button).visibility = View.GONE
            }
            else
            {
                view.findViewById<Button>(R.id.join_button).visibility = View.VISIBLE
            }


            if(event.players.contains(user))
            {
                view.findViewById<Button>(R.id.join_button).isEnabled = true
                view.findViewById<Button>(R.id.join_button).text = "Leave"
            }
            else if (event.maxPlayers.toInt() == event.players.size)
            {
                view.findViewById<Button>(R.id.join_button).text = "Full"
            }
            else
            {
                view.findViewById<Button>(R.id.join_button).text = "Join"
            }
        }

        fun checkIfJoinned(user: User, playerList: ArrayList<User>): Boolean
        {
            return playerList.contains(user)
        }
        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

            val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

            setUpButton(holder.view, currentUser!!, events[position])


            setImage(holder.view, events[position].sport)

            val databaseRef = FirebaseDatabase.getInstance().getReference("events")

            val dateFormat = SimpleDateFormat("hh:mm a yyyy-MM-dd")

            holder.view.findViewById<TextView>(R.id.date).text = dateFormat.format(events[position].date)
            holder.view.findViewById<TextView>(R.id.sport).text = events[position].sport
            holder.view.findViewById<TextView>(R.id.location).text = events[position].location
            holder.view.findViewById<TextView>(R.id.host).text = "Host " + events[position].host.username



            val distance = getDistance(events[position].location)

            if (distance == null)
            {
                holder.view.findViewById<TextView>(R.id.distance).text = "Distance: Not Found"
            }
            else if (distance!! < 1)
            {
                holder.view.findViewById<TextView>(R.id.distance).text = "Distance: < 1 mi"

            }
            else
            {
                holder.view.findViewById<TextView>(R.id.distance).text = "Distance " + String.format("%.1f", getDistance(events[position].location)) + " mi"

            }


            holder.view.itemView.setOnClickListener {

                model.setvalue(events[position].event_id,events[position], getDistance(events[position].location).toString())

                openFragment(EventsDisplayFragment())
            }


            val popup = PopupMenu(context,holder.view.findViewById(R.id.options))

            val menuListener = PopupMenu.OnMenuItemClickListener {

                when(it.title)
                {
                    "Delete Game" -> {

                        databaseRef.child(events[position].event_id).removeValue().addOnSuccessListener {

                            Toast.makeText(mainActivity, "Game has been deleted", Toast.LENGTH_SHORT).show()
                        }
                        return@OnMenuItemClickListener true
                    }

                    "Edit Game" -> {

                        model.setvalue(events[position].event_id,events[position], getDistance(events[position].location).toString())

                        openFragment(UpdateEventFragment())
                        return@OnMenuItemClickListener true
                    }
                    "View Game" -> {

                        model.setvalue(events[position].event_id,events[position], getDistance(events[position].location).toString())

                        openFragment(EventsDisplayFragment())
                        return@OnMenuItemClickListener true

                    }

                    else -> {
                        return@OnMenuItemClickListener false
                    }
                }
            }

            popup.setOnMenuItemClickListener(menuListener)

            val inflater = popup.menuInflater

            if (currentUser.equals(events[position].host))
            {
                inflater.inflate(R.menu.actions_creator, popup.menu)
            }
            else
            {
                inflater.inflate(R.menu.actions_public, popup.menu)
            }

            holder.view.findViewById<ImageButton>(R.id.options).setOnClickListener {

                popup.show()

            }

            holder.view.findViewById<Button>(R.id.join_button).setOnClickListener {

                if(!checkIfJoinned(currentUser, events[position].players))
                {
                    var newList = events[position].players
                    newList.add(currentUser)
                    joinEvent(events[position], newList)
                }
                else {

                    leaveEvent(events[position], currentUser)
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
