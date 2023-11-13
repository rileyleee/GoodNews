package com.saveurlife.goodnews.models

import android.provider.ContactsContract.CommonDataKinds.Phone
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyMemInfo(): RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var phone: String = ""
    var lastConnection: RealmInstant = RealmInstant.from(0,0)
    var state: Char = '0'
    var location: Location? = null
    var familyId: Long = 0

    constructor(id: String, name:String, phone: String, lastConnection:RealmInstant, state:Char, location: Location, familyId: Long): this(){
        this.id = id
        this.name = name
        this.phone = phone
        this.lastConnection = lastConnection
        this.state = state
        this.location = location
        this.familyId = familyId
    }
}