package com.saveurlife.goodnews.api

// Request
data class RequestPlaceStateInfo(val buttonType:Boolean, val text:String, val lat:Double, val lon:Double, val date:String)
data class RequestPlaceDate(val date: String)

// Response
data class ResponseFacilityRegist(val success:Boolean, val message:String, val data:Map<String, Any>)
data class ResponseAllFacilityState(val success: Boolean, val message: String, val data: ArrayList<FacilityState>)
data class ResponseDurationFacilityState(val success: Boolean, val message: String, val data: ArrayList<DurationFacilityState>)

data class FacilityState(val createDate:String, val placeId:Int, val buttonType: Boolean, val text: String, val lat:Double, val lon:Double, val lastModifiedDate:String)
data class DurationFacilityState(val id: Int, val buttonType: Boolean, val text:String, val lat:Double, val lon:Double, val lastModifiedDate: String)
