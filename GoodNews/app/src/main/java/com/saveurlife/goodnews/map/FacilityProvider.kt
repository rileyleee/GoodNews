package com.saveurlife.goodnews.map

import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class FacilityProvider {

    private lateinit var realm: Realm

    fun getFacilityData(){

        realm = Realm.open(GoodNewsApplication.realmConfiguration)

        val facilities: RealmResults<OffMapFacility> = realm.query<OffMapFacility>().find()


    }


}