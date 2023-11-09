package com.saveurlife.goodnews.map

import android.content.Context
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint


class FacilityProvider(private val context: Context) {

    // 이후에 대피소, 병원, 식료품점으로 구분하여 함수 생성
    fun getFacilityData(): List<LabelledGeoPoint> {

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)

        val facilities: RealmResults<OffMapFacility> = realm.query<OffMapFacility>().find()

        // 데이터 확인
        facilities.forEach { fac ->
//            Log.v(
//                "FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
        }

        // 변수에 담기
        val points = facilities.map { fac ->
            LabelledGeoPoint(fac.latitude.toDouble(), fac.longitude.toDouble(), fac.name)
        }

        realm.close()

        return points
    }
}