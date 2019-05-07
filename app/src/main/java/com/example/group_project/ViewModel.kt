package com.example.group_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ViewModel: ViewModel() {

    private var time = MutableLiveData<String>()
    private var sport = MutableLiveData<String>()
    private var location = MutableLiveData<String>()
    private var userName = MutableLiveData<String>()
    private var distance = MutableLiveData<String>()


    fun setvalue(time : String, sport: String, location: String, userName: String, distance : String){
        this.time.value = time
        this.sport.value = sport
        this.location.value = location
        this.userName.value = userName
        this.distance.value = distance

    }

    fun getTime(): MutableLiveData<String>{

        return time
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

//    holder.view.findViewById<TextView>(R.id.date).text = events[position].time
//    holder.view.findViewById<TextView>(R.id.sport).text = events[position].sport
//    holder.view.findViewById<TextView>(R.id.location).text = events[position].location
//    holder.view.findViewById<TextView>(R.id.host).text = "Host " + events[position].host.username
//    holder.view.findVie
}