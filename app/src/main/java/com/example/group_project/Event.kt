package com.example.group_project

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Event (event_id: String, sport: String, date: Date, location : String, maxPlayers: String, host: User, players: ArrayList<User> ) : Serializable {

    var event_id: String
    var sport: String
    var date: Date
    var location : String
    var maxPlayers: String
    var host: User
    var players: ArrayList<User>

    init {
        this.event_id = event_id
        this.sport = sport
        this.location = location
        this.date = date
        this.maxPlayers = maxPlayers
        this.host = host
        this.players = players
    }

    constructor() : this("","", Date(),"","" , User(), arrayListOf<User>())
}