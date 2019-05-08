package com.example.group_project

import java.util.*

class Event (event_id: String, sport: String, calendar: Calendar, location : String, players: String, host: User) {

    var event_id: String
    var sport: String
    var calendar: Calendar
    var location : String
    var players: String
    var host: User

    init {
        this.event_id = event_id
        this.sport = sport
        this.location = location
        this.calendar = calendar
        this.players = players
        this.host = host
    }

    constructor() : this("","", Calendar.getInstance(),"","", User())
}