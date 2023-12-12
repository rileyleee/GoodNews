package com.saveurlife.goodnews.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FamilyInterface {

    // 가족 모임장소 사용 여부 수정 (시설 사용할수 있는지 여부)
    @PUT("family/app/placeuse/{placeId}")
    fun getFamilyUpdatePlaceCanUse(@Path("placeId") placeId: Int, @Body placeCanUse: RequestBody):Call<ResponsePlaceUseUpdate>

    // 가족 모임장소 수정
    @PUT("family/app/placeinfo/{placeId}")
    fun getFamilyUpdatePlaceInfo(@Path("placeId") placeId: Int, @Body placeModify: RequestBody):Call<ResponsePlaceUpdate>

    // 가족 신청 수락 및 거절 (true-거절, false-수락 // 거절하면 삭제 주의)
    @PUT("family/app/acceptfamily")
    fun updateRegistFamily(@Body accept: RequestBody):Call<ResponseFamilyUpdate>

    // 가족 모임 장소 등록
    @POST("family/app/registplace")
    fun registFamilyPlace(@Body placeRegist: RequestBody):Call<ResponsePlaceRegist>

    // 가족 신청 요청 (전화 번호로 가족 신청 요청)
    @POST("family/app/registfamily")
    fun registFamily(@Body familyRegist: RequestBody):Call<ResponseFamilyRegist>

    // 가족 모임장소 상세 조회 (특정 모임장소를 조회함)
    @POST("family/app/placeinfo")
    fun getFamilyPlaceInfoDetail(@Body placeId: RequestBody):Call<ResponsePlaceDetail>

    // 가족 신청 요청 조회 (내가 받은 가족 신청 조회)
    @POST("family/app/getregistfamily")
    fun getRegistFamily(@Body memberId: RequestBody):Call<ResponseAccept>

    // 가족 구성원 조회
    @POST("family/app/familyinfo")
    fun getFamilyMemberInfo(@Body memberId: RequestBody):Call<ResponseMemberInfo>

    //가족 모임장소 조회 (내 가족의 모든 리스트를 간단히 가져옴)
    @POST("family/app/allplaceinfo")
    fun getFamilyPlaceInfo(@Body memberId: RequestBody):Call<ResponsePlaceInfo>
}