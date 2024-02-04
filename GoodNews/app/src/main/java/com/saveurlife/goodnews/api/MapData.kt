package com.saveurlife.goodnews.api

// Request
// yyyy-mm-dd hh:mm:ss
data class RequestPlaceStateInfo(val id : String, val buttonType:Boolean, val text:String, val lat:Double, val lon:Double, val date:String)
data class RequestPlaceDate(val date: String)

// Response
data class ResponseFacilityRegist(val success:Boolean, val message:String, val data:Map<String, Any>)
data class ResponseAllFacilityState(val success: Boolean, val message: String, val data: ArrayList<FacilityState>)
data class ResponseDurationFacilityState(val success: Boolean, val message: String, val data: ArrayList<DurationFacilityState>)

// 2024-01-24T14:40:15
data class FacilityState(val createDate:String, val placeId:Int, val buttonType: Boolean, val text: String, val lat:Double, val lon:Double)
data class DurationFacilityState(val id: String, val buttonType: Boolean, val text:String, val lat:Double, val lon:Double, val createdDate: String)
