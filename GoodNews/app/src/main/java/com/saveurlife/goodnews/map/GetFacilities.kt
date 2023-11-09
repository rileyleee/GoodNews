package com.saveurlife.goodnews.map

import android.content.Context
import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import org.osmdroid.util.GeoPoint

class GetFacilities(private val context: Context) {

    // 이후에 대피소, 병원, 식료품점으로 구분하여 함수 생성
    fun getFacilityData(): List<GeoPoint>{

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)


        val facilities: RealmResults<OffMapFacility> = realm.query<OffMapFacility>().find()
        for(fac in facilities) {
            Log.v(
                "GetFacData",
                "반복문 facility: ${fac.name} latitude: ${fac.latitude}"
            )
        }

        return facilities.map { facility ->
            val geoPoint = GeoPoint(facility.latitude, facility.longitude)
            Log.v("FacilityMapping", "리턴문 facility: ${facility.name}, GeoPoint: $geoPoint")
            geoPoint
        }
    }


}