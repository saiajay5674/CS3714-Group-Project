package com.example.group_project



import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.net.ConnectivityManager
import android.os.Handler
import androidx.appcompat.widget.SwitchCompat
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
import kotlinx.android.synthetic.main.fragment_events_display.*
import kotlinx.coroutines.delay
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventsListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var database: FirebaseDatabase
    lateinit var adapter: EventListAdapter
    lateinit var geocoder: Geocoder
    lateinit var locationManager: LocationManager
    lateinit var mainActivity: MainActivity
    lateinit var currentUser: User
    lateinit var window: PopupWindow

    lateinit var usernameSwitch: SwitchCompat
    lateinit var distanceSwitch: SwitchCompat
    lateinit var timeSwitch: SwitchCompat
    lateinit var playersSwitch: SwitchCompat
    lateinit var filterButton: Button
    lateinit var filterCancelButton: Button
    lateinit var radiusSeekBar: SeekBar
    var radiusFilter: Int = 25
    lateinit var sortByEnum: SORT
    var events = ArrayList<Event>()



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


        if(!isNetworkAvailable())
        {
            val intent = Intent(mainActivity, NoInternetFragment::class.java)
            val bundle = Bundle()
            bundle.putInt("open", 2)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.events_list)
        geocoder = Geocoder(context, Locale.getDefault())

        val recyclerView = view.findViewById<RecyclerView>(R.id.events_list)
        adapter = EventListAdapter()
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

        window = PopupWindow(mainActivity)

        val v = layoutInflater.inflate(R.layout.filter_popup, null)

        window.contentView = v

        window.isOutsideTouchable = true
        window.isFocusable = true

        distanceSwitch = v.findViewById(R.id.chipDistance)
        timeSwitch = v.findViewById(R.id.chipTime)
        usernameSwitch = v.findViewById(R.id.chipUserName)
        playersSwitch = v.findViewById(R.id.chipGame)

        sortByEnum = model.getSortBy().value!!

        setUpSwitches(sortByEnum)


        filterButton = v.findViewById(R.id.filter_button)
        filterCancelButton = v.findViewById(R.id.filter_cancel)
        radiusSeekBar = v.findViewById(R.id.radius_seekbar)

        radiusSeekBar.max = 106
        radiusSeekBar.progress = model.getRadiusFilter().value!!
        radiusFilter = model.getRadiusFilter().value!!


        if(events.size > 0)
        {
            filterRadius(events)
        }

        v.findViewById<TextView>(R.id.radius).text = "Radius: " + radiusSeekBar.progress.toString() + " mi"



        currentUser = model.getCurrentUser().value!!

        val ref = database.getReference("events")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {

                events = ArrayList()
                val calendar = Calendar.getInstance().time

                if (p0!!.exists())
                {
                    for(event in p0.children)
                    {
                        val event = event.getValue(Event::class.java)

                        if (event!!.date.compareTo(calendar) > 0)
                        {
                            events.add(event!!)
                        }
                    }
                }
                filterRadius(events)
                adapter.sortBy(sortByEnum)
            }

        })

        distanceSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

            if(b)
            {
                timeSwitch.isChecked = false
                usernameSwitch.isChecked = false
                playersSwitch.isChecked = false
            }
        }

        timeSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

            if(b)
            {
                distanceSwitch.isChecked = false
                usernameSwitch.isChecked = false
                playersSwitch.isChecked = false
            }
        }
        usernameSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

            if(b)
            {
                timeSwitch.isChecked = false
                distanceSwitch.isChecked = false
                playersSwitch.isChecked = false

            }
        }
        playersSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

            if(b)
            {
                timeSwitch.isChecked = false
                distanceSwitch.isChecked = false
                usernameSwitch.isChecked = false

            }
        }


        radiusSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {


                val newProgress = (Math.round(progress/5.0)*5).toInt()

                seekBar?.progress = newProgress

                if (newProgress == 105)
                {
                    v.findViewById<TextView>(R.id.radius).text = "Radius: Max mi"
                }
                else if (newProgress == 0)
                {
                    v.findViewById<TextView>(R.id.radius).text = "Radius: < 1 mi"
                }
                else
                {
                    v.findViewById<TextView>(R.id.radius).text = "Radius: " + radiusSeekBar.progress.toString() + " mi"
                }





            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Nothing
            }

        })


        filterCancelButton.setOnClickListener {

            setUpSwitches(sortByEnum)

            radiusSeekBar.progress = radiusFilter
            window.dismiss()
        }

        filterButton.setOnClickListener {

            if(timeSwitch.isChecked)
            {
                sortByEnum = SORT.TIME
            }
            if(distanceSwitch.isChecked)
            {
                sortByEnum = SORT.DISTANCE
            }
            if(usernameSwitch.isChecked)
            {
                sortByEnum = SORT.USERNAME
            }
            if(playersSwitch.isChecked)
            {
                sortByEnum = SORT.PLAYERS
            }

            radiusFilter = radiusSeekBar.progress
            filterRadius(events)
            adapter.sortBy(sortByEnum)
            model.setFilter(sortByEnum, radiusFilter)
            window.dismiss()
        }

        view.findViewById<ImageButton>(R.id.filterBtn).setOnClickListener {

            window.showAsDropDown(view.findViewById(R.id.filterBtn))

        }

        return view
    }

    fun setUpSwitches(sortBy: SORT)
    {
        when(sortBy)
        {
            SORT.USERNAME -> {

                timeSwitch.isChecked = false
                distanceSwitch.isChecked = false
                usernameSwitch.isChecked = true
                playersSwitch.isChecked = false
            }

            SORT.DISTANCE -> {

                timeSwitch.isChecked = false
                distanceSwitch.isChecked = true
                usernameSwitch.isChecked = false
                playersSwitch.isChecked = false
            }

            SORT.TIME -> {

                timeSwitch.isChecked = true
                distanceSwitch.isChecked = false
                usernameSwitch.isChecked = false
                playersSwitch.isChecked = false
            }

            SORT.PLAYERS -> {

                timeSwitch.isChecked = false
                distanceSwitch.isChecked = false
                usernameSwitch.isChecked = false
                playersSwitch.isChecked = true
            }
        }
    }

    fun filterRadius(list: ArrayList<Event>)
    {
        val newList = ArrayList<Event>()

        var radius: Int

        if (radiusFilter == 105)
        {
            radius = Int.MAX_VALUE
        }
        else if (radiusFilter == 0)
        {
            radius = 1
        }
        else
        {
            radius = radiusFilter
        }
        for (event in list)
        {

            try {

                val distance = adapter.getDistance(event.location)

                if (distance!!.toInt() <= radius)
                {
                    newList.add(event)
                }

            } catch (e: java.lang.Exception)
            {
                newList.add(event)
            }
        }

        adapter.setEvents(newList)
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
                return null
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
                        .load("https://media.istockphoto.com/vectors/shuttlecock-for-badminton-from-bird-feathers-vector-id519780012?k=6&m=519780012&s=612x612&w=0&h=Zp-pwanjZTM9vUksfiuE1laChfRrg6rhUn_7_gSL3zY=")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Volleyball" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://clipartion.com/wp-content/uploads/2015/10/volleyball-ball-clipart-free-clip-art-images.jpg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Soccer" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://i.pinimg.com/originals/e7/d7/19/e7d7190f0b5b3abd4f6c17e2c7989ec3.jpg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Football" -> {
                    Glide.with(this@EventsListFragment)
                        .load("https://images.gutefrage.net/media/fragen/bilder/wie-nennt-man-die-weisse-schnur-an-einem-football/0_original.jpg?v=1449783687000")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
                "Golf" -> {
                    Glide.with(this@EventsListFragment)
                        .load("http://designatprinting.com/wp-content/uploads/golf-ball-clipart-no-background-clipartxtras-1800-16.jpeg")
                        .apply(RequestOptions().override(128, 128)).into(view.findViewById(R.id.event_image))
                }
            }
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
            progressDialog.setMessage("Leaving Game...")
            progressDialog.setCanceledOnTouchOutside(false)
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

        internal fun sortBy(sortBy: SORT)
        {
            when(sortBy)
            {
                SORT.USERNAME -> {

                    events = events.sortedWith(compareBy { it.host.username })

                }

                SORT.DISTANCE -> {

                    events = events.sortedWith(compareBy { getDistance(it.location) })
                }

                SORT.TIME -> {

                    events = events.sortedWith(compareBy { it.date })
                }
                SORT.PLAYERS -> {
                    events = events.sortedWith(compareByDescending { it.players.size })
                }
            }
            notifyDataSetChanged()
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


    private fun isNetworkAvailable(): Boolean
    {
        try {

            val cm = mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
        }
        catch (e: java.lang.Exception)
        {
            return false
        }
    }


}
