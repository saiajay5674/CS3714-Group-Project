package com.example.group_project


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap

//    private val mapCallBack = OnMapReadyCallback {
//        mMap = it
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
        // Inflate the layout for this fragment


    (view.findViewById(R.id.popBtn) as Button).setOnClickListener{



        val dialog = Dialog()

        //dialog.setTargetFragment(this,1)
        dialog.show(childFragmentManager, "dialog")

        //val builder = AlertDialog.Builder(context)
//        // Get the layout inflater
//        val inflater = requireActivity().layoutInflater;
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.popup, null))
//            // Add action buttons
//            .setPositiveButton("dd",
//                DialogInterface.OnClickListener { dialog, id ->
//                    // sign in the user ...
//                })
//            .setNegativeButton("cancel",
//                DialogInterface.OnClickListener { dialog, id ->
//                    dialog.cancel()
//                })
//        builder.create()
//     ?: throw IllegalStateException("Activity cannot be null")
    }

//    (view.findViewById(R.id.timePick) as? Button).setOnClickListener{
//        val calander = Calendar.getInstance()
//
//        val selectedTimeListner = TimePickerDialog.OnTimeSetListener{timePicker, hour, minute ->
//            calander.set(Calendar.HOUR_OF_DAY, hour)
//            calander.set(Calendar.MINUTE, minute)
//
//           timeWindow.text = SimpleDateFormat("HH:mm").format(calander.time)
//        }
//        TimePickerDialog(context,selectedTimeListner,calander.get(Calendar.HOUR_OF_DAY), calander.get(Calendar.MINUTE),true).show()
//
//    }

    val spinner: Spinner?= (getView()?.findViewById(R.id.add_event_sport) as? Spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter.createFromResource(context, R.array.games, android.R.layout.simple_spinner_item).also { adapter ->
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner?.adapter = adapter
    }


    return view
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

}
