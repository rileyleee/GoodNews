package com.saveurlife.goodnews.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MapInterface {

    // 지도 시설 상태 등록
    @POST("map/registfacility")
    fun registMapFacility(@Body placeStateInfo: RequestBody): Call<ResponseFacilityRegist>

    // 지도 시설 상세 보기
    @POST("map/facilitydetail")
    fun findDetailFacilityInfo(@Body facilityId: RequestBody): Call<ResponseFacilityDetail>
}