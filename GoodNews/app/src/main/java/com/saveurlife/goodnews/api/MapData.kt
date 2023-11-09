package com.saveurlife.goodnews.api

// Request
data class RequestPlaceStateInfo(val buttonType:Boolean, val text:String, val lat:Double, val lon:Double)

// Response
data class ResponseFacilityRegist(val success:Boolean, val message:String, val data:Map<String, Any>)


