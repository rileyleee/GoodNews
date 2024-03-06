package com.saveurlife.goodnews.tmap

import android.location.Location

data class tMapFacilityData(
    val location: Location,
    val type: String,
    val name: String)
