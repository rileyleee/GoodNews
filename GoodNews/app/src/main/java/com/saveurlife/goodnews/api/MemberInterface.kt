package com.saveurlife.goodnews.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberInterface {

    // 멤버 정보 수정
    @PUT("members/{memberId}")
    fun updateMemberInfo(@Path("memberId") memberId: String, @Body memberInfo: RequestBody): Call<ResponseModifyMember>

    // 멤버 상태 정보 수정
    @POST("members/state/{memberId}")
    fun updateMemberState(@Path("memberId") memberId:String, @Body state:RequestBody):Call<ResponseState> // requestState

    // 멤버 위치 및 연결시각 업데이트
    @POST("members/get/{memberId}")
    fun updateMember(@Path("memberId") memberId:String, @Body state:RequestBody):Call<ResponseLocation> //requestLocation

    // 멤버 정보 조회
    @POST("members/search")
    fun findMemberInfo(@Body memberId: RequestBody): Call<ResponseMember>


    // 추가 정보 등록
    @POST("members/registinfo")
    fun registMemberInfo(@Body memberAddInfo: RequestBody): Call<ResponseRegistMember>

    // 최초 로그인 유무 조회
    @POST("members/firstlogin")
    fun firstLoginInfo(@Body memberId: RequestBody): Call<ResponseLogin>

}
