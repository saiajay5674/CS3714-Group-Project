package com.example.group_project


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude = 0.toDouble()
    private var longitude = 0.toDouble()

    private lateinit var lastLocation: Location
    private var marker: Marker? = null

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback


    private lateinit var mSearchText: EditText

    companion object {
        private const val PERMISSION_CODE: Int = 1000
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mSearchText =  view.findViewById(R.id.input_search)


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

        (view.findViewById(R.id.popBtn) as FloatingActionButton).setOnClickListener {
            val dialog = EventDialog()

            //dialog.setTargetFragment(this,1)
            dialog.show(childFragmentManager, "dialog")
        }

        val spinner: Spinner? = (getView()?.findViewById(R.id.edit_event_sport) as? Spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter.createFromResource(context, R.array.games, android.R.layout.simple_spinner_item).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spinner?.adapter = adapter
        }
        init()
        return view
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                lastLocation = p0!!.locations.get(p0!!.locations.size - 1) // get last location

                if (marker != null) {
                    marker!!.remove()
                }

                latitude = lastLocation.latitude
                longitude = lastLocation.longitude

                val latLng = LatLng(latitude, longitude)

                var markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("current Position")
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

            //TODO: move camera automatically when location is enabled
            //no Effect???
            mMap!!.isMyLocationEnabled = true

        }
    }

    private fun init()
    {
        mSearchText.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN || event?.action == KeyEvent.KEYCODE_ENTER)
                {
                    //searching
                    geoLocate()
                }

                return false
            }

        })
    }

    private fun geoLocate()
    {
        val searchString = mSearchText.text.toString().trim()
        val geocoder = Geocoder(activity)
        var list = emptyList<Address>()

        try {
            list = geocoder.getFromLocationName(searchString, 1)
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        val marker = MarkerOptions().position(latLng).title(title)
        mMap.addMarker(marker)
        hideSoftKeyboard()
    }

    private fun hideSoftKeyboard()
    {
        this.activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    override fun onStop() {
//        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}
