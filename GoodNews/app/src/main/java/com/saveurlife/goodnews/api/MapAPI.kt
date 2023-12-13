package com.saveurlife.goodnews.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
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
    fun registMapFacility(buttonType: Boolean, text:String, lat:Double, lon:Double, date: String){
        val data = RequestPlaceStateInfo(buttonType, text, lat, lon, date)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = mapService.registMapFacility(requestBody)

        Log.d("tesssss", data.toString())
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
                    val errorBodyString = response.errorBody()?.string()

                    if (errorBodyString != null) {
                        try {
                            val errorJson = JSONObject(errorBodyString)
                            val code = errorJson.getInt("status")
                            val message = errorJson.getString("error")

                            Log.d("API ERROR", "Error Code: $code, Message: $message")

                        } catch (e: JSONException) {
                            Log.e("API ERROR", "Error parsing JSON: $errorBodyString", e)
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")
                    }
                }
            }
            override fun onFailure(call: Call<ResponseFacilityRegist>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    // 지도 시설 상태 조회
    fun getAllMapFacility(callback: FacilityStateCallback) {
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





                        if(data != null){
                            callback.onSuccess(data)
                        }
                    }else{
                        Log.d("API ERROR", "값이 안왔음.")
                    }
                } else {
                    Log.d("API ERROR", response.toString())
                    val errorBodyString = response.errorBody()?.string()

                    // 오류가 빈값(공백)으로 보내옴..

                    if (errorBodyString != null) {
                        try {
                            val errorJson = JSONObject(errorBodyString)
                            val code = errorJson.getInt("code")
                            val message = errorJson.getString("message")

                            Log.d("API ERROR", "Error Code: $code, Message: $message")

                        } catch (e: JSONException) {
                            Log.d("API ERROR", "Error parsing JSON: $errorBodyString", e)
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")
                    }
                }
            }
            override fun onFailure(call: Call<ResponseAllFacilityState>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    // 기간 이후 시설 상태 조회
    // 주의 사항!!!!!
    // "2023-11-12 03:12:02" 이런 형태로 보내셔야 합니다.
    fun getDurationFacility(date: String, callback: FacilityStateDurationCallback){
        val data = RequestPlaceDate(date)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = mapService.getDurationFacility(requestBody)

        // response
        call.enqueue(object : Callback<ResponseDurationFacilityState> {
            override fun onResponse(call: Call<ResponseDurationFacilityState>, response: Response<ResponseDurationFacilityState>) {
                if(response.isSuccessful){
                    val responseBody = response.body()

                    Log.d("API RESP", responseBody.toString())

                    // 받아온 데이터에 대한 응답을 처리
                    if(responseBody!=null){
                        val data = responseBody.data
                        // 원하는 작업을 여기에 추가해 주세요.






                        callback.onSuccess(data)
                    }else{
                        Log.d("API ERROR", "값이 안왔음.")
                    }
                } else {
                    Log.d("API ERROR", response.toString())
                    val errorBodyString = response.errorBody()?.string()

                    if (errorBodyString != null) {
                        try {
                            val errorJson = JSONObject(errorBodyString)
                            val code = errorJson.getInt("status")
                            val message = errorJson.getString("error")

                            Log.d("API ERROR", "Error Code: $code, Message: $message")

                        } catch (e: JSONException) {
                            Log.e("API ERROR", "Error parsing JSON: $errorBodyString", e)
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")
                    }
                }
            }
            override fun onFailure(call: Call<ResponseDurationFacilityState>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }
    interface FacilityStateCallback {
        fun onSuccess(result: ArrayList<FacilityState>)
        fun onFailure(error:String)
    }
    interface FacilityStateDurationCallback {
        fun onSuccess(result: ArrayList<DurationFacilityState>)
        fun onFailure(error:String)
    }

}

