package com.saveurlife.goodnews.map

import android.content.Context
import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FacilityUIType
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint


class FacilityProvider(private val context: Context) {

    // 오프라인 시설 정보 반환(대분류)
    fun getFilteredFacilities(category: FacilityUIType): List<OffMapFacility> {


        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val facsData: RealmResults<OffMapFacility>

        if (category == FacilityUIType.HOSPITAL) {
            facsData = realm.query<OffMapFacility>("type=$0", "병원").find()
            Log.d("facilityProvider", "병원 찾아요")
        } else if (category == FacilityUIType.GROCERY) {
            facsData = realm.query<OffMapFacility>("type=$0 OR type = $1", "편의점", "마트").find()
            Log.d("facilityProvider", "편의점이랑 마트 찾아요")
        } else {
            facsData = realm.query<OffMapFacility>().find()
            Log.d("facilityProvider", "전체 찾아요")
        }

        Log.v("facilityProvider의 카테고리", "$category")


        // 결과를 새로운 리스트로 복사합니다.
        val facList = facsData.map { fac ->
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

        return facList
    }
    
    // 오프라인 시설 정보 반환(소분류)
    fun getFilteredFacilitiesBySubCategory(subCategory: String): List<OffMapFacility> {

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)

        val facsData: RealmResults<OffMapFacility> =
            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "$subCategory").find()

        Log.d("facilityProvider", "$subCategory 찾아요")


        // 결과를 새로운 리스트로 복사합니다.
        val facList = facsData.map { fac ->
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

        return facList
    }


    // 오프라인 시설 오버레이 위한 좌표 반환
    fun getFacilityGeoData(category: FacilityUIType): List<LabelledGeoPoint> {

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)

        // 민방위 대피소

        val sheltersT1: RealmResults<OffMapFacility> =
            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "$category").find()

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

        realm.close()

        return points
    }
}