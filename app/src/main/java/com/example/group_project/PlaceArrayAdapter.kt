package com.example.group_project

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds

import java.util.ArrayList
import java.util.concurrent.TimeUnit

class PlaceArrayAdapter(context: Context, resource: Int, private val mBounds: LatLngBounds, private val mPlaceFilter: AutocompleteFilter) : ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete>(context, resource), Filterable {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mResultList: ArrayList<PlaceAutocomplete>? = null

    fun setGoogleApiClient(googleApiClient: GoogleApiClient?) {
        if (googleApiClient == null || !googleApiClient.isConnected) {
            mGoogleApiClient = null
        } else {
            mGoogleApiClient = googleApiClient
        }
    }

    override fun getCount(): Int {
        return mResultList!!.size
    }

    override fun getItem(position: Int): PlaceAutocomplete? {
        return mResultList!![position]
    }

    private fun getPredictions(constraint: CharSequence?): ArrayList<PlaceAutocomplete>? {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint!!)
            val results = Places.GeoDataApi
                .getAutocompletePredictions(
                    mGoogleApiClient!!, constraint.toString(),
                    mBounds, mPlaceFilter
                )
            // Wait for predictions, set the timeout.
            val autocompletePredictions = results
                .await(60, TimeUnit.SECONDS)
            val status = autocompletePredictions.status
            if (!status.isSuccess) {
                Toast.makeText(
                    context, "Error: " + status.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(
                    TAG, "Error getting place predictions: " + status
                        .toString()
                )
                autocompletePredictions.release()
                return null
            }

            Log.i(
                TAG, "Query completed. Received " + autocompletePredictions.count
                        + " predictions."
            )
            val iterator = autocompletePredictions.iterator()
            val resultList = ArrayList<PlaceAutocomplete>(autocompletePredictions.count)
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                resultList.add(PlaceAutocomplete(prediction.placeId.toString(), prediction.getFullText(null))
                )
            }
            // Buffer release
            autocompletePredictions.release()
            return resultList
        }
        Log.e(TAG, "Google API client is not connected.")
        return null
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    mResultList = getPredictions(constraint)
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    inner class PlaceAutocomplete(var placeId: CharSequence, var description: CharSequence) {

        override fun toString(): String {
            return description.toString()
        }
    }

    companion object {
        private val TAG = "PlaceArrayAdapter"
    }
}