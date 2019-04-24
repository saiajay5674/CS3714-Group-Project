package com.example.group_project

class Event (event_id: String, sport: String, time: String, location : String, players: String, host: String) {

    var event_id: String
    var sport: String
    var time: String
    var location : String
    var players: String
    var host: String

    init {
        this.event_id = event_id
        this.sport = sport
        this.location = location
        this.time = time
        this.players = players
        this.host = host
    }

    constructor() : this("","","","","", "")
}