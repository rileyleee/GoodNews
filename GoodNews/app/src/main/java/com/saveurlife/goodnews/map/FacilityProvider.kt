package com.saveurlife.goodnews.map

import android.content.Context
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FacilityUIType
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint


class FacilityProvider(private val context: Context) {

    private val copiedAll = GoodNewsApplication.FacilityDataManager.copiedAll
    private val copiedShelter = GoodNewsApplication.FacilityDataManager.copiedShelter
    private val copiedHospital = GoodNewsApplication.FacilityDataManager.copiedHospital
    private val copiedGrocery = GoodNewsApplication.FacilityDataManager.copiedGrocery
    private val copiedMinbangwi = GoodNewsApplication.FacilityDataManager.copiedMinbangwi
    private val copiedEarthquake = GoodNewsApplication.FacilityDataManager.copiedEarthquake


    // 오프라인 시설 정보 반환(대분류)
    fun getFilteredFacilities(category: FacilityUIType): MutableList<OffMapFacility> {

        //Log.v("facilityProvider의 카테고리", "$category")
        return when (category) {
            FacilityUIType.HOSPITAL -> {
                //Log.v("copiedHospital", "copiedHospital.size: ${copiedHospital.size}")
                //Log.v("copiedHospital","${copiedHospital[1].name}/${copiedHospital[1].type}")
                copiedHospital
            }

            FacilityUIType.GROCERY -> {
                //Log.v("copiedGrocery", "copiedGrocery.size: ${copiedGrocery.size}")
                //Log.v("copiedGrocery","${copiedGrocery[1].name}/${copiedGrocery[1].type}")
                copiedGrocery
            }
            FacilityUIType.SHELTER ->{
                //Log.v("copiedShelter", "copiedShelter.size: ${copiedShelter.size}")
                //Log.v("copiedShelter","${copiedShelter[1].name}/${copiedShelter[1].type}")
                copiedShelter
            }

            else -> {
                //Log.v("copiedAll", "copiedAll.size: ${copiedAll.size}")
                //Log.v("copiedAll","${copiedAll[1].name}/${copiedAll[1].type}")
                copiedAll
            }
        }
    }

    // 오프라인 시설 정보 반환(소분류)
    fun getFilteredFacilitiesBySubCategory(subCategory: String): MutableList<OffMapFacility> {

        return when (subCategory) {
            "민방위" -> {
                //Log.v("copiedMinbangwi", "copiedMinbangwi.size: ${copiedMinbangwi.size}")
                //Log.v("copiedMinbangwi","${copiedMinbangwi[1].name}/${copiedMinbangwi[1].type}")
                copiedMinbangwi
            }

            "지진해일" -> {
                //Log.v("copiedEarthquake", "copiedEarthquake.size: ${copiedEarthquake.size}")
                //Log.v("copiecopiedEarthquakedAll","${copiedEarthquake[1].name}/${copiedEarthquake[1].type}")
                copiedEarthquake
            }

            "전체" -> {
                //Log.v("copiedEarthquake", "copiedEarthquake.size: ${copiedEarthquake.size}")
                //Log.v("copiecopiedEarthquakedAll","${copiedEarthquake[1].name}/${copiedEarthquake[1].type}")
                copiedShelter
            }

            else -> {
                //Log.d("facilityProvider", "기본 값은 전체")
                //Log.v("copiedShelter", "copiedShelter.size: ${copiedShelter.size}")
                //Log.v("copiedShelter","${copiedShelter[1].name}/${copiedShelter[1].type}")
                copiedShelter
            }
        }
    }

    // 오프라인 시설 오버레이 위한 좌표 반환
    fun getFacilityGeoData(category: FacilityUIType): List<LabelledGeoPoint> {

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)

        // 민방위 대피소

        val sheltersT1: RealmResults<OffMapFacility> =
            realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "$category").find()

        // 변수에 담기
        val points = sheltersT1.map { fac ->
            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
        }

        realm.close()

        return points
    }
}