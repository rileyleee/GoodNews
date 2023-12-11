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

    private val copiedAll = mutableListOf<OffMapFacility>()
    private val copiedShelter = mutableListOf<OffMapFacility>()
    private val copiedHospital = mutableListOf<OffMapFacility>()
    private val copiedGrocery = mutableListOf<OffMapFacility>()
    private val copiedMinbangwi = mutableListOf<OffMapFacility>()
    private val copiedEarthquake = mutableListOf<OffMapFacility>()

    init {
        loadDataFromRealm()
    }

    private fun loadDataFromRealm() {
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val facsDataAll = realm.query<OffMapFacility>().find()
        val shelter = realm.query<OffMapFacility>("type = $0", "대피소").find()
        val hospital = realm.query<OffMapFacility>("type = $0 OR type = $1", "약국","병원").find()
        val grocery = realm.query<OffMapFacility>("type = $0 OR type = $1", "편의점", "마트").find()
        val minbangwi = realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "민방위").find()
        val earthquake = realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "지진해일").find()

        facsDataAll.forEach { fac ->
            copiedAll.add(copyFacsData(fac))
        }
        shelter.forEach { fac ->
            copiedShelter.add(copyFacsData(fac))
        }
        hospital.forEach { fac ->
            copiedHospital.add(copyFacsData(fac))
        }
        grocery.forEach { fac ->
            copiedGrocery.add(copyFacsData(fac))
        }
        minbangwi.forEach { fac ->
            copiedMinbangwi.add(copyFacsData(fac))
        }
        earthquake.forEach { fac ->
            copiedEarthquake.add(copyFacsData(fac))
        }

        realm.close()
    }

    // 오프라인 시설 정보 반환(대분류)
    fun getFilteredFacilities(category: FacilityUIType): MutableList<OffMapFacility> {

        //Log.v("facilityProvider의 카테고리", "$category")
        return when (category) {
            FacilityUIType.HOSPITAL -> {
                //Log.d("facilityProvider", "병원, 약국 찾았어요")
                //Log.v("copiedHospital", "copiedHospital.size: ${copiedHospital.size}")
                //Log.v("copiedHospital","${copiedHospital[1].name}/${copiedHospital[1].type}")
                copiedHospital
            }

            FacilityUIType.GROCERY -> {
                //Log.d("facilityProvider", "마트, 편의점 찾았어요")
                //Log.v("copiedGrocery", "copiedGrocery.size: ${copiedGrocery.size}")
                //Log.v("copiedGrocery","${copiedGrocery[1].name}/${copiedGrocery[1].type}")
                copiedGrocery
            }
            FacilityUIType.SHELTER ->{
                //Log.d("facilityProvider", "대피소 찾았어요")
                //Log.v("copiedShelter", "copiedShelter.size: ${copiedShelter.size}")
                //Log.v("copiedShelter","${copiedShelter[1].name}/${copiedShelter[1].type}")
                copiedShelter
            }

            else -> {
                //Log.d("facilityProvider", "기본 값은 전체")
                //Log.v("copiedAll", "copiedAll.size: ${copiedAll.size}")
                //Log.v("copiedAll","${copiedAll[1].name}/${copiedAll[1].type}")
                copiedAll
            }
        }
    }

    private fun getFamilyLocation() {
        TODO("Not yet implemented")
    }

    // 오프라인 시설 정보 반환(소분류)
    fun getFilteredFacilitiesBySubCategory(subCategory: String): MutableList<OffMapFacility> {

        return when (subCategory) {
            "민방위" -> {
                //Log.d("facilityProvider", "민방위 찾았어요")
                //Log.v("copiedMinbangwi", "copiedMinbangwi.size: ${copiedMinbangwi.size}")
                //Log.v("copiedMinbangwi","${copiedMinbangwi[1].name}/${copiedMinbangwi[1].type}")
                copiedMinbangwi
            }

            "지진해일" -> {
                //Log.d("facilityProvider", "지진해일 찾았어요")
                //Log.v("copiedEarthquake", "copiedEarthquake.size: ${copiedEarthquake.size}")
                //Log.v("copiecopiedEarthquakedAll","${copiedEarthquake[1].name}/${copiedEarthquake[1].type}")
                copiedEarthquake
            }

            "전체" -> {
                //Log.d("facilityProvider", "대피소 찾았어요")
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

    fun copyFacsData(data: OffMapFacility): OffMapFacility {
        return OffMapFacility().apply {
            this.id = data.id
            this.name = data.name
            this.type = data.type
            this.latitude = data.latitude
            this.longitude = data.longitude
            this.canUse = data.canUse
            this.addInfo = data.addInfo
        }
    }
}