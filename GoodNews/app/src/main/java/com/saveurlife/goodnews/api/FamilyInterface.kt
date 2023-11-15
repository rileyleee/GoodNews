package com.saveurlife.goodnews.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FamilyInterface {

    // 가족 모임장소 사용 여부 수정
    @PUT("family/app/placeuse/{placeId}")
    fun getFamiliyUpdatePlaceCanUse(@Path("placeId") placeId: Int, @Body placeCanUse: RequestBody):Call<ResponsePlaceUseUpdate>

    // 가족 모임장소 수정
    @PUT("family/app/placeinfo/{placeId}")
    fun getFamilyUpdatePlaceInfo(@Path("placeId") placeId: Int, @Body placeModify: RequestBody):Call<ResponsePlaceUpdate>

    // 가족 신청 수락 및 거절
    @PUT("family/app/acceptfamily")
    fun updateRegistFamily(@Body accept: RequestBody):Call<ResponseFamilyUpdate>

    // 가족 모임 장소 등록
    @POST("family/app/registplace")
    fun registFamilyPlace(@Body placeRegist: RequestBody):Call<ResponsePlaceRegist>

    // 가족 신청 요청
    @POST("family/app/registfamily")
    fun registFamily(@Body familyRegist: RequestBody):Call<ResponseFamilyRegist>

    // 가족 모임장소 상세 조회
    @POST("family/app/placeinfo")
    fun getFamilyPlaceInfoDetail(@Body placeId: RequestBody):Call<ResponsePlaceDetail>

    // 가족 신청 요청 조회
    @POST("family/app/getregistfamily")
    fun getRegistFamily(@Body memberId: RequestBody):Call<ResponseAccept>

    // 가족 구성원 조회
    @POST("family/app/familyinfo")
    fun getFamilyMemberInfo(@Body memberId: RequestBody):Call<ResponseMemberInfo>

    //가족 모임장소 조회
    @POST("family/app/allplaceinfo")
    fun getFamilyPlaceInfo(@Body memberId: RequestBody):Call<ResponsePlaceInfo>
}