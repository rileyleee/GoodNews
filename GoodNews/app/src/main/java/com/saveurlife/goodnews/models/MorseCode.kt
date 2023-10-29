package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class MorseCode(): RealmObject {
    @PrimaryKey
    var id: Int =0
    var text: String = ""
    constructor(id:Int, text:String):this(){
        this.id=id
        this.text=text
    }
}