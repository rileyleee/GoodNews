package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyMemInfo(): RealmObject {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var memberId: Long = 0
    var lastConnection: RealmInstant = RealmInstant.from(0,0)
    var state: Char = '0'
    var location: Location? = null
    var familyId: Long = 0

    constructor(id: Int, name:String, memberId: Long, lastConnection:RealmInstant, state:Char, location: Location, familyId: Long): this(){
        this.id = id
        this.name = name
        this.memberId = memberId
        this.lastConnection = lastConnection
        this.state = state
        this.location = location
        this.familyId = familyId
    }
}