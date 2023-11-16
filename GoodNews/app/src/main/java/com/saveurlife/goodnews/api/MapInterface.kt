package com.saveurlife.goodnews.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MapInterface {

    // 지도 시설 상태 등록 (사용자 위험 정보 등록)
    @POST("facility/app/registfacility")
    fun registMapFacility(@Body placeStateInfo: RequestBody): Call<ResponseFacilityRegist>

    // 지도 시설 상태 조회 (사용자가 저장한 모든 위험 정보 조회)
    @POST("facility/app/getfacility")
    fun getAllMapFacility():Call<ResponseAllFacilityState>

    // 기간 이후 시설 상태 조회 (특정 시점 이후 위험정보 조회 - 동기화 용도)
    @POST("facility/app/afterfacility")
    fun getDurationFacility(@Body placeDate:RequestBody):Call<ResponseDurationFacilityState>
}