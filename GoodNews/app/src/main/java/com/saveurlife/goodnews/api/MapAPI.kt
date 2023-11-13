package com.saveurlife.goodnews.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapAPI {

    // Retrofit 인스턴스 생성
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.saveurlife.kr/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val mapService = retrofit.create(MapInterface::class.java)
    private val gson = Gson()
    private val mediaType = "application/json; charset=utf-8".toMediaType()


    // 지도 시설 상태 등록
    fun registMapFacility(buttonType: Boolean, text:String, lat:Double, lon:Double){
        val data = RequestPlaceStateInfo(buttonType, text, lat, lon)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = mapService.registMapFacility(requestBody)

        // response
        call.enqueue(object : Callback<ResponseFacilityRegist> {
            override fun onResponse(call: Call<ResponseFacilityRegist>, response: Response<ResponseFacilityRegist>) {
                if(response.isSuccessful){
                    val responseBody = response.body()

                    Log.d("API RESP", responseBody.toString())

                    // 받아온 데이터에 대한 응답을 처리
                    if(responseBody!=null){
                        val data = responseBody.data
                        // 원하는 작업을 여기에 추가해 주세요.







                    }else{
                        Log.d("API ERROR", "값이 안왔음.")
                    }
                } else {
                    Log.d("API ERROR", response.toString())
                }
            }
            override fun onFailure(call: Call<ResponseFacilityRegist>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    // 지도 시설 상태 조회
    fun getAllMapFacility(){
        // request
        val call = mapService.getAllMapFacility()
        // response
        call.enqueue(object : Callback<ResponseAllFacilityState> {
            override fun onResponse(call: Call<ResponseAllFacilityState>, response: Response<ResponseAllFacilityState>) {
                if(response.isSuccessful){
                    val responseBody = response.body()

                    Log.d("API RESP", responseBody.toString())

                    // 받아온 데이터에 대한 응답을 처리
                    if(responseBody!=null){
                        val data = responseBody.data
                        // 원하는 작업을 여기에 추가해 주세요.







                    }else{
                        Log.d("API ERROR", "값이 안왔음.")
                    }
                } else {
                    Log.d("API ERROR", response.toString())
                }
            }
            override fun onFailure(call: Call<ResponseAllFacilityState>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })

    }

}