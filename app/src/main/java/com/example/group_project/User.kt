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
}