package com.example.group_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*


class ViewModel: ViewModel() {

    private var date = MutableLiveData<Date>()
    private var sport = MutableLiveData<String>()
    private var location = MutableLiveData<String>()
    private var userName = MutableLiveData<String>()
    private var distance = MutableLiveData<String>()

    fun setvalue(date : Date, sport: String, location: String, userName: String, distance : String){
        this.date.value = date
        this.sport.value = sport
        this.location.value = location
        this.userName.value = userName
        this.distance.value = distance

    }

    fun getCalendar(): MutableLiveData<Date>{

        return date
    }

    fun getSport(): MutableLiveData<String>{

        return sport
    }

    fun getLocation(): MutableLiveData<String>{

        return location
    }

    fun getUserName(): MutableLiveData<String>{

        return userName
    }

    fun getDistance(): MutableLiveData<String>{

        return  distance
    }
}