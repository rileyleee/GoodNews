package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyPlace() : RealmObject {
    @PrimaryKey
    var placeId: Int = 0
    var name: String = ""
    var address: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var canUse: Boolean = true
    var seq: Int = 0
    var lastUpdate: RealmInstant = RealmInstant.from(0, 0)

    constructor(
        placeId: Int,
        name: String,
        address: String,
        latitude: Double,
        longitude: Double,
        canUse: Boolean,
        seq: Int,
        lastUpdate: RealmInstant
    ) : this() {
        this.placeId = placeId
        this.name = name
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.canUse = canUse
        this.seq = seq
        this.lastUpdate = lastUpdate
    }

}