package com.example.group_project


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude = 0.toDouble()
    private var longitude = 0.toDouble()

    private var location:String  = ""

    private lateinit var lastLocation: Location
    private var marker: Marker? = null

    lateinit var geocoder: Geocoder

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    lateinit var locationManager: LocationManager
    var currentLocation: Location? = null
    lateinit var mainActivity: MainActivity


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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_map, container, false)


        locationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, locationListener)

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)


        geocoder = Geocoder(context, Locale.getDefault())

        val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

        location = model.getLocation()

        var geoLocation: Address

        try {
            geoLocation = geocoder.getFromLocationName(location, 1)[0]

            latitude = geoLocation.latitude
            longitude = geoLocation.longitude

        }catch (e: IOException){

        }


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


        //Request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
        } else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

        (view.findViewById(R.id.plusBtn) as FloatingActionButton).setOnClickListener {
            val dialog = EventDialog()

            //dialog.setTargetFragment(this,1)
            dialog.show(childFragmentManager, "dialog")
        }


        view.findViewById<FloatingActionButton>(R.id.locationBtn).setOnClickListener {

            latitude = currentLocation!!.latitude
            longitude = currentLocation!!.longitude


            geoLocate()
        }

        return view
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                lastLocation = p0!!.locations.get(p0!!.locations.size - 1) // get last location

                if (marker != null) {
                    marker!!.remove()
                }

                val model = activity?.run{ ViewModelProviders.of(this).get(ViewModel::class.java)}?: throw Exception("Invalid Activity")

                if (!model.checkLocationChnaged()){

                    if (marker != null) {
                        marker!!.remove()
                    }

                latitude = lastLocation.latitude
                longitude = lastLocation.longitude
               }

                val latLng = LatLng(latitude, longitude)

                var markerOptions = MarkerOptions()
                    .position(latLng)
                    //  .icon(BitmapDescriptorFactory.fromAsset(R.drawable.ic_magnify.toString()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                marker = mMap.addMarker((markerOptions))

                //Move Camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                // mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
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

                            buildLocationRequest()
                            buildLocationCallBack()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                                Looper.myLooper())

                            mMap!!.isMyLocationEnabled = true
                        }
                } else {
                    Toast.makeText(context!!, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Initialize GooglePlay Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                mMap!!.isMyLocationEnabled = false // true??
            }
        } else {

            mMap!!.isMyLocationEnabled = true

        }
    }


    private fun geoLocate()
    {
        if (marker != null) {
            marker!!.remove()
        }


        val geocoder = Geocoder(activity)
        var list = emptyList<Address>()

        try {
            list = geocoder.getFromLocation(latitude, longitude, 1)
        }
        catch (e: IOException)
        {

        }

        if (list.size > 0)
        {
            val address = list.get(0)

            moveCamera(LatLng(address.latitude, address.longitude), 15f, address.getAddressLine(0))

        }

    }

    private fun moveCamera(latLng: LatLng, zoom: Float, title: String)
    {
        if (marker != null) {
            marker!!.remove()
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        val marker = MarkerOptions().position(latLng).title(title)
        mMap.addMarker(marker)
    }

}
