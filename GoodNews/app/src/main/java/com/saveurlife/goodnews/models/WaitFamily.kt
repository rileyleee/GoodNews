package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject

class WaitFamily() : RealmObject {
    var name: String = ""
    var phoneNumber : String = ""

    constructor(
        name : String,
        phoneNumber: String
    ):this(){
        this.name = name
        this.phoneNumber = phoneNumber
    }
}