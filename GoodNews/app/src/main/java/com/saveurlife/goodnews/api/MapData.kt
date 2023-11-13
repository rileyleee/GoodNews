package com.saveurlife.goodnews.api

// Request
data class RequestPlaceStateInfo(val buttonType:Boolean, val text:String, val lat:Double, val lon:Double)

// Response
data class ResponseFacilityRegist(val success:Boolean, val message:String, val data:Map<String, Any>)
data class ResponseAllFacilityState(val success: Boolean, val message: String, val data: ArrayList<FacilityState>)

data class FacilityState(val createDate:String, val placeId:Int, val buttonType: Boolean, val text: String, val lat:Double, val lon:Double, val lastModifiedDate:String)

