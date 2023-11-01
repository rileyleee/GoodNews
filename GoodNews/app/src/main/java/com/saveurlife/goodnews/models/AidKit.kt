package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AidKit() : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var state: String = ""

    constructor(id: Int, name: String, state: String) : this() {
        this.id = id
        this.name = name
        this.state = state
    }
}