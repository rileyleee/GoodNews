package com.saveurlife.goodnews.api

// Request
data class RequestPlaceStateInfo(val buttonType:Boolean, val text:String, val lat:Double, val lon:Double)
data class RequestFacilityId(val facilityId: String)

// Response
data class ResponseFacilityRegist(val success:Boolean, val message:String, val data:Map<String, Any>)
data class ResponseFacilityDetail(val success: Boolean, val message: String, val data:MapInfo)

data class MapInfo(val type:String, val name:String, val lat:Double, val lon:Double, val canuse:Int, val facility:FacilityInfo)
data class FacilityInfo(val 시설면적:Double, val 대피소종류:String)

