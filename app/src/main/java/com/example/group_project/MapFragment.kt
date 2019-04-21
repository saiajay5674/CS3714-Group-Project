package com.example.group_project


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


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

    companion object {
        private const val PERMISSION_CODE: Int = 1000
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_map, container, false)

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

        (view.findViewById(R.id.popBtn) as Button).setOnClickListener {
            val dialog = EventDialog()

            //dialog.setTargetFragment(this,1)
            dialog.show(childFragmentManager, "dialog")
        }

        val spinner: Spinner? = (getView()?.findViewById(R.id.add_event_sport) as? Spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter.createFromResource(context, R.array.games, android.R.layout.simple_spinner_item).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spinner?.adapter = adapter
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

                latitude = lastLocation.latitude
                longitude = lastLocation.longitude

                val latLng = LatLng(latitude, longitude)

                var markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("current Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                marker = mMap.addMarker((markerOptions))

                //Move Camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                // mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
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
        } else

            //TODO: move camera automatically when location is enabled
            //no Effect???
           mMap!!.isMyLocationEnabled = true

        //Enable zoom in and out control
        mMap.uiSettings.isZoomControlsEnabled = true


    }

    override fun onStop() {
//        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        super.onStop()
    }
}
