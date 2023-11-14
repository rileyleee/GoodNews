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


    fun getFacilityData(): List<OffMapFacility>{

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val facsData: RealmResults<OffMapFacility> =
            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "민방위 대피소").find()


        // 결과를 새로운 리스트로 복사합니다.
        val sheltersT1 = facsData.map { fac ->
            OffMapFacility().apply {
                id = fac.id
                type = fac.type
                name = fac.name
                longitude = fac.longitude
                latitude = fac.latitude
                canUse = fac.canUse
                addInfo = fac.addInfo
            }
        }
        realm.close()

        return sheltersT1
    }



    // 이후에 대피소, 병원, 식료품점으로 구분하여 함수 생성
    fun getFacilityGeoData(): List<LabelledGeoPoint> {

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

//        val medicals: RealmResults<OffMapFacility> = realm.query<OffMapFacility>("type=$0 OR type = $1","병원","약국").find()
//
//        // 데이터 확인
//        medicals.forEach { fac ->
//            Log.v("FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = medicals.map { fac ->
//            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
//        }

        // 대피소 전체
//        val shelters: RealmResults<OffMapFacility> =
//            realm.query<OffMapFacility>("type=$0", "대피소").find()
//
//        // 데이터 확인
//        shelters.forEach { fac ->
//            Log.v(
//                "FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = shelters.map { fac ->
//            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
//        }

//        // 민방위 대피소

        val sheltersT1: RealmResults<OffMapFacility> =
            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "민방위 대피소").find()

        // 데이터 확인
//        SheltersT1.forEach { fac ->
//            Log.v(
//                "FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
        // 변수에 담기
        val points = sheltersT1.map { fac ->
            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
        }

        // 지진해일대피소

//        val sheltersT2: RealmResults<OffMapFacility> =
//            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "지진해일대피소").find()
//
//        // 데이터 확인
//        sheltersT2.forEach { fac ->
//            Log.v(
//                "FacDataFromRealm",
//                "facility: ${fac.name}, latitude: ${fac.latitude}, longitude: ${fac.longitude}"
//            )
//        }
//        // 변수에 담기
//        val points = sheltersT2.map { fac ->
//            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
//        }


        realm.close()

        return points
    }
}