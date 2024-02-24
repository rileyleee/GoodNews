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

    private val copiedAll = GoodNewsApplication.FacilityDataManager.copiedAll
    private val copiedShelter = GoodNewsApplication.FacilityDataManager.copiedShelter
    private val copiedHospital = GoodNewsApplication.FacilityDataManager.copiedHospital
    private val copiedGrocery = GoodNewsApplication.FacilityDataManager.copiedGrocery
    private val copiedMinbangwi = GoodNewsApplication.FacilityDataManager.copiedMinbangwi
    private val copiedEarthquake = GoodNewsApplication.FacilityDataManager.copiedEarthquake

    init {
        // 데이터 로드를 백그라운드에서 실행 -> 이렇게 하면 리사이클러 뷰에서 리스트에 담기 전에 로드하려고 해서 터짐
        // 1안 메인 액티비티에서 해봐야하나? 앱 실행 시 한번만 하고 fragment 간 이동할 때는 실행하지 않도록 하면 안되나
        // 2안 현재 메인 액티비티에서 mapfragment로 이동하는 때에 실행하도록 구현해놓음 -> map fragment 이동 시 마다 로딩^0^
        // 3안 (적용안) application에서 싱글톤으로 구현하여 한 번만 로드하고 호출해서 사용 아래 주석된 코드는 application 파일에서 작업 중

//        GlobalScope.launch(Dispatchers.IO) {
//            loadDataFromRealm()
//            Log.i("**FACILITY PROVIDER 초기 호출","데이터 리스트 작업 완료")
//            // 대략 6초 7초 소요 -> 하지만 이 코드는 초기화 전에 리사이클러뷰에서 호출하기 때문에 터지는 거임 시간 확인 위한 용도!
//        }

        // 기존 코드
        // loadDataFromRealm()
    }

//    private fun loadDataFromRealm() {
//        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
//        val facsDataAll = realm.query<OffMapFacility>().find()
//        val shelter = realm.query<OffMapFacility>("type = $0", "대피소").find()
//        val hospital = realm.query<OffMapFacility>("type = $0 OR type = $1", "약국","병원").find()
//        val grocery = realm.query<OffMapFacility>("type = $0 OR type = $1", "편의점", "마트").find()
//        val minbangwi = realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "민방위").find()
//        val earthquake = realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "지진해일").find()
//
//        facsDataAll.forEach { fac ->
//            copiedAll.add(copyFacsData(fac))
//        }
//        shelter.forEach { fac ->
//            copiedShelter.add(copyFacsData(fac))
//        }
//        hospital.forEach { fac ->
//            copiedHospital.add(copyFacsData(fac))
//        }
//        grocery.forEach { fac ->
//            copiedGrocery.add(copyFacsData(fac))
//        }
//        minbangwi.forEach { fac ->
//            copiedMinbangwi.add(copyFacsData(fac))
//        }
//        earthquake.forEach { fac ->
//            copiedEarthquake.add(copyFacsData(fac))
//        }
//
//        realm.close()
//    }

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

        // 변수에 담기
        val points = sheltersT1.map { fac ->
            LabelledGeoPoint(fac.latitude, fac.longitude, fac.name)
        }

        realm.close()

        return points
    }

//    fun copyFacsData(data: OffMapFacility): OffMapFacility {
//        return OffMapFacility().apply {
//            this.id = data.id
//            this.name = data.name
//            this.type = data.type
//            this.latitude = data.latitude
//            this.longitude = data.longitude
//            this.canUse = data.canUse
//            this.addInfo = data.addInfo
//        }
//    }
}