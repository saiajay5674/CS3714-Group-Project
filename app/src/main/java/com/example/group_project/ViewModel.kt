package com.example.group_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList


class ViewModel: ViewModel() {


    private var distance = MutableLiveData<String>()
    private var event = MutableLiveData<Event>()
    private var event_id = MutableLiveData<String>()

    fun setvalue(event_id: String, event: Event, distance: String){

        this.event_id.value = event_id
        this.distance.value = distance
        this.event.value = event

    }

    fun getEvent(): MutableLiveData<Event>
    {
        return event
    }

    fun getEventId(): MutableLiveData<String>
    {
        return event_id
    }

    fun getDistance(): MutableLiveData<String>
    {
        return distance
    }

}