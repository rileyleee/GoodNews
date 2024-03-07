package com.saveurlife.goodnews.tmap

import android.content.Context
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FacilityUIType
import com.saveurlife.goodnews.models.OffMapFacility

class TMapFacilityProvider(private val context: Context) {
    private val copiedAll = GoodNewsApplication.FacilityDataManager.copiedAll
    private val copiedShelter = GoodNewsApplication.FacilityDataManager.copiedShelter
    private val copiedHospital = GoodNewsApplication.FacilityDataManager.copiedHospital
    private val copiedGrocery = GoodNewsApplication.FacilityDataManager.copiedGrocery
    private val copiedMinbangwi = GoodNewsApplication.FacilityDataManager.copiedMinbangwi
    private val copiedEarthquake = GoodNewsApplication.FacilityDataManager.copiedEarthquake

    fun getFilteredFacilities(category: TMapFacilityUiType): MutableList<OffMapFacility> {

        //Log.v("facilityProvider의 카테고리", "$category")
        return when (category) {
            TMapFacilityUiType.HOSPITAL -> {
                //Log.v("copiedHospital", "copiedHospital.size: ${copiedHospital.size}")
                //Log.v("copiedHospital","${copiedHospital[1].name}/${copiedHospital[1].type}")
                copiedHospital
            }

            TMapFacilityUiType.GROCERY -> {
                copiedGrocery
            }
            TMapFacilityUiType.SHELTER ->{
                copiedShelter
            }

            else -> {
                copiedAll
            }
        }
    }

    // 오프라인 시설 정보 반환(소분류)
    fun getFilteredFacilitiesBySubCategory(subCategory: String): MutableList<OffMapFacility> {
        return when (subCategory) {
            "민방위" -> {
                copiedMinbangwi
            }

            "지진해일" -> {
                copiedEarthquake
            }

            "전체" -> {
                copiedShelter
            }

            else -> {
                copiedShelter
            }
        }
    }
}