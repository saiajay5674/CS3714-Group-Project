package com.example.group_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList


class ViewModel: ViewModel() {

    private var date = MutableLiveData<Date>()
    private var sport = MutableLiveData<String>()
    private var location = MutableLiveData<String>()
    private var userName = MutableLiveData<String>()
    private var distance = MutableLiveData<String>()
    private var players = MutableLiveData<ArrayList<User>>()
    private var maxPlayers = MutableLiveData<String>()

    fun setvalue(date : Date, sport: String, location: String, userName: String, maxPlayers: String, distance : String, players: ArrayList<User>){
        this.date.value = date
        this.sport.value = sport
        this.location.value = location
        this.userName.value = userName
        this.distance.value = distance
        this.players.value = players
        this.maxPlayers.value = maxPlayers

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

    fun getMaxPlayers(): MutableLiveData<String>{

        return  maxPlayers
    }

    fun getPlayers(): MutableLiveData<ArrayList<User>>{

        return  players
    }
}