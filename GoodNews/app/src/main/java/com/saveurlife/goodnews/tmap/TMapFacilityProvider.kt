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
                //Log.v("copiedGrocery", "copiedGrocery.size: ${copiedGrocery.size}")
                //Log.v("copiedGrocery","${copiedGrocery[1].name}/${copiedGrocery[1].type}")
                copiedGrocery
            }
            TMapFacilityUiType.SHELTER ->{
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
}