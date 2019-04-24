package com.example.group_project

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class User(uid: String, username: String, emailAddress: String, phoneNumber: String) : Serializable {


    var uid: String
    var username: String
    var emailAddress: String
    var phoneNumber: String

    init {
        this.uid = uid
        this.username = username
        this.emailAddress = emailAddress
        this.phoneNumber = phoneNumber
    }

    constructor() : this("", "", "", "")

    override fun equals(other: Any?): Boolean {


        if(other == null)
        {
            return false
        }
        else if (other.javaClass != User::class.java)
        {
            return false
        }
        else {

            var user: User = other as User

            return this.uid == user.uid
        }
    }
}