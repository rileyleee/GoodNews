package com.saveurlife.goodnews.api


// request
data class RequestMemberId(val memberId:String)
data class RequestState(val state:String)
data class RequestLocation(val lat: Double, val lon:Double)
data class RequestMemberInfo(val name:String, val birthDate:String, val bloodType:String, val addInfo:String)
data class RequestMemberAddInfo(val memberId:String, val name:String, val birthDate:String, val gender:String, val bloodType:String, val addInfo:String)

// response
data class ResponseLogin(val success: Boolean, val message: String, val data:FirstLogin)
data class ResponseState(val success: Boolean, val message: String, val data:Map<String, Any>)
data class ResponseLocation(val success: Boolean, val message: String, val data: Map<String, Any>)
data class ResponseModifyMember(val success: Boolean, val message: String, val data:MemberModifyInfo)
data class ResponseMember(val success:Boolean, val message:String, val data:MemberInfo)
data class ResponseRegistMember(val success: Boolean, val message: String, val data: MemberRegistInfo)
data class MemberInfo(val memberId:String, val name:String, val birthDate: String, val gender: String, val bloodType: String, val addInfo: String, val state:String, val lat:Double, val lon:Double)
data class MemberRegistInfo(val memberId: String, val name: String, val birthDate: String, val gender: String, val bloodType: String, val addInfo: String)
data class MemberModifyInfo(val name: String, val birthDate: String, val bloodType: String, val addInfo: String)
data class FirstLogin(val firstLogin: Boolean)




