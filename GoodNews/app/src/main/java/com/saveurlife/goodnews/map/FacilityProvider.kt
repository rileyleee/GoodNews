package com.saveurlife.goodnews.map

import android.content.Context
import android.util.Log
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

        // 시설 전체
//        val facilities: RealmResults<OffMapFacility> = realm.query<OffMapFacility>().find()
//        // 데이터 확인
//        facilities.forEach { fac ->
//            Log.v("FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = facilities.map { fac ->
//            LabelledGeoPoint(fac.latitude.toDouble(), fac.longitude.toDouble(), fac.name)
//        }

        // 식료품

//        val FNBs: RealmResults<OffMapFacility> = realm.query<OffMapFacility>("type=$0 OR type = $1","편의점","마트").find()
//
//        // 데이터 확인
//        FNBs.forEach { fac ->
//            Log.v("FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = FNBs.map { fac ->
//            LabelledGeoPoint(fac.latitude.toDouble(), fac.longitude.toDouble(), fac.name)
//        }

        // 의료시설

//        val Medicals: RealmResults<OffMapFacility> = realm.query<OffMapFacility>("type=$0 OR type = $1","병원","약국").find()
//
//        // 데이터 확인
//        Medicals.forEach { fac ->
//            Log.v("FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = Medicals.map { fac ->
//            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
//        }

        // 대피소 전체
//        val Shelters: RealmResults<OffMapFacility> =
//            realm.query<OffMapFacility>("type=$0", "대피소").find()
//
//        // 데이터 확인
//        Shelters.forEach { fac ->
//            Log.v(
//                "FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = Shelters.map { fac ->
//            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
//        }

//        // 민방위 대피소

        val SheltersT1: RealmResults<OffMapFacility> =
            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "민방위 대피소").find()

        // 데이터 확인
        SheltersT1.forEach { fac ->
            Log.v(
                "FacDataFromRealm",
                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
            )
        }
        // 변수에 담기
        val points = SheltersT1.map { fac ->
            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
        }

        // 지진해일대피소

//        val SheltersT2: RealmResults<OffMapFacility> =
//            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "지진해일대피소").find()
//
//        // 데이터 확인
//        SheltersT2.forEach { fac ->
//            Log.v(
//                "FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = SheltersT2.map { fac ->
//            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
//        }


        realm.close()

        return points
    }
}