package com.saveurlife.goodnews.api


// request
data class RequestMemberInfo(val name:String, val gender: String, val birthDate:String, val bloodType:String, val addInfo:String, val lat:Double, val lon: Double)
data class RequestState(val state:String)
data class RequestLocation(val lat: Double, val lon:Double)
data class RequestMemberId(val memberId:String)
data class RequestMemberAddInfo(val memberId:String, val phoneNumber:String, val name:String, val birthDate:String, val gender:String, val bloodType:String, val addInfo:String)

// response
data class ResponseModifyMember(val success: Boolean, val message: String, val data: Map<String, Any>)
data class ResponseState(val success: Boolean, val message: String, val data:Map<String, Any>)
data class ResponseLocation(val success: Boolean, val message: String, val data: Map<String, Any>)
data class ResponseMember(val success:Boolean, val message:String, val data:MemberInfo)
data class ResponseRegistMember(val success: Boolean, val message: String, val data: MemberRegistInfo)
data class ResponseLogin(val success: Boolean, val message: String, val data:FirstLogin)


data class MemberInfo(val memberId:String, val phoneNumber:String, val name:String, val birthDate: String, val gender: String, val bloodType: String, val addInfo: String, val state:String, val lat:Double, val lon:String, val familyId: String)
data class MemberRegistInfo(val lastUpdate:String, val memberId: String, val phoneNumber: String, val name: String, val birthDate: String, val gender: String, val bloodType: String, val addInfo: String, val state:String, val lat:Double, val lon:String, val password:String, val locationTime:String, val role:String, val lastConnection:String)
data class FirstLogin(val firstLogin: Boolean)




