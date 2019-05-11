package com.example.group_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList


class ViewModel: ViewModel() {


    private var distance = MutableLiveData<String>()
    private var event = MutableLiveData<Event>()
    private var event_id = MutableLiveData<String>()
    private var currentUser = MutableLiveData<User>()
    private var sortByEnum = MutableLiveData<SORT>()
    private var radiusFilter = MutableLiveData<Int>()

    fun setvalue(event_id: String, event: Event, distance: String){

        this.event_id.value = event_id
        this.distance.value = distance
        this.event.value = event

    }

    fun getSortBy(): MutableLiveData<SORT>
    {
        return sortByEnum
    }

    fun getRadiusFilter(): MutableLiveData<Int>
    {
        return radiusFilter
    }

    fun intitializeApp(user: User, sort: SORT, radius: Int)
    {
        this.currentUser.value = user
        this.sortByEnum.value = sort
        this.radiusFilter.value = radius
    }

    fun setFilter(sort: SORT, radius: Int)
    {
        this.sortByEnum.value = sort
        this.radiusFilter.value = radius
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

    fun getCurrentUser(): MutableLiveData<User>
    {
        return currentUser
    }
}