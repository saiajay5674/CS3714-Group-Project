package com.example.group_project

import java.util.*

class Event (event_id: String, sport: String, date: Date, location : String, players: String, host: User) {

    var event_id: String
    var sport: String
    var date: Date
    var location : String
    var players: String
    var host: User

    init {
        this.event_id = event_id
        this.sport = sport
        this.location = location
        this.date = date
        this.players = players
        this.host = host
    }

    constructor() : this("","", Date(),"","", User())
}