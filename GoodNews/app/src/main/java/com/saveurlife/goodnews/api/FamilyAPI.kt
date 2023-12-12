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

class FamilyAPI {

    // Retrofit 인스턴스 생성
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.saveurlife.kr/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // api 인터페이스 구현체 생성
    private val familyService = retrofit.create(FamilyInterface::class.java)
    private val gson = Gson()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    // 가족 모임장소 사용 여부 수정
    fun getFamilyUpdatePlaceCanUse(placeId:Int, canUse: Boolean){
        // request
        val data = RequestPlaceCanUse(canUse)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = familyService.getFamilyUpdatePlaceCanUse(placeId, requestBody)
        call.enqueue(object : Callback<ResponsePlaceUseUpdate> {
            override fun onResponse(call: Call<ResponsePlaceUseUpdate>, response: Response<ResponsePlaceUseUpdate>) {
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
            override fun onFailure(call: Call<ResponsePlaceUseUpdate>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })

    }

    // 가족 모임장소 수정
    fun getFamilyUpdatePlaceInfo(placeId:Int, name: String, lat: Double, lon: Double){
        // request
        val data = RequestPlaceInfo(name, lat, lon)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = familyService.getFamilyUpdatePlaceInfo(placeId, requestBody)
        call.enqueue(object : Callback<ResponsePlaceUpdate> {
            override fun onResponse(call: Call<ResponsePlaceUpdate>, response: Response<ResponsePlaceUpdate>) {
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
            override fun onFailure(call: Call<ResponsePlaceUpdate>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })

    }

    // 가족 신청 수락
    fun updateRegistFamily(familyMemberId:Int, refuse:Boolean){
        // request
        val data = RequestAccept(familyMemberId, refuse)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        Log.d("test", data.toString())
        val call = familyService.updateRegistFamily(requestBody)
        call.enqueue(object : Callback<ResponseFamilyUpdate> {
            override fun onResponse(call: Call<ResponseFamilyUpdate>, response: Response<ResponseFamilyUpdate>) {
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
            override fun onFailure(call: Call<ResponseFamilyUpdate>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    // 가족 모임 장소 등록
    fun registFamilyPlace(memberId: String, name: String, lat: Double, lon: Double, seq:Int, address:String, callback:RegistFamilyCallback){
        // request
        val data = RequestPlaceDetailInfo(memberId, name, lat, lon, seq, address)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = familyService.registFamilyPlace(requestBody)
        call.enqueue(object : Callback<ResponsePlaceRegist> {
            override fun onResponse(call: Call<ResponsePlaceRegist>, response: Response<ResponsePlaceRegist>) {
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
            override fun onFailure(call: Call<ResponsePlaceRegist>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    // 가족 신청 요청
    fun registFamily(memberId:String, otherPhone:String, callback: FamilyRegistrationCallback){
        // request
        val data = RequestFamilyRegist(memberId, otherPhone)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        Log.d("test", data.toString())
        val call = familyService.registFamily(requestBody)
        call.enqueue(object : Callback<ResponseFamilyRegist> {
            override fun onResponse(call: Call<ResponseFamilyRegist>, response: Response<ResponseFamilyRegist>) {
                if(response.isSuccessful){
                    val responseBody = response.body()

                    Log.d("API RESP", responseBody.toString())

                    // 받아온 데이터에 대한 응답을 처리
                    if(responseBody!=null){
                        val data = responseBody.data
                        // 원하는 작업을 여기에 추가해 주세요.






                        callback.onSuccess(data.familyId)
                    }else{
                        Log.d("API ERROR", "값이 안왔음.")
                        callback.onFailure("No data received")
                    }
                } else {
                    Log.d("API ERROR", response.toString())

                    val errorBodyString = response.errorBody()?.string()

                    if (errorBodyString != null) {
                        try {
                            val errorJson = JSONObject(errorBodyString)
                            val code = errorJson.getInt("code")
                            val message = errorJson.getString("message")

                            Log.d("API ERROR", "Error Code: $code, Message: $message")
                            callback.onFailure(message)
                        } catch (e: JSONException) {
                            Log.e("API ERROR", "Error parsing JSON: $errorBodyString", e)
                            callback.onFailure("Error parsing JSON")
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")
                        callback.onFailure("Error body is null")
                    }
                }
            }
            override fun onFailure(call: Call<ResponseFamilyRegist>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    // 가족 모임장소 상세 조회
    fun getFamilyPlaceInfoDetail(placeId: Int, callback: FamilyPlaceDetailCallback){
        // request
        val data = RequestPlaceId(placeId)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = familyService.getFamilyPlaceInfoDetail(requestBody)
        call.enqueue(object : Callback<ResponsePlaceDetail> {
            override fun onResponse(call: Call<ResponsePlaceDetail>, response: Response<ResponsePlaceDetail>) {
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
            override fun onFailure(call: Call<ResponsePlaceDetail>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    interface FamilyPlaceDetailCallback {
        fun onSuccess(result: PlaceDetailInfo)
        fun onFailure(error:String)
    }

    // 가족 신청 요청 조회
    fun getRegistFamily(memberId: String, callback: WaitListCallback) {
        // request
        val data = RequestMemberId(memberId)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)
        var resp: ArrayList<WaitInfo>? = null

        val call = familyService.getRegistFamily(requestBody)
        call.enqueue(object : Callback<ResponseAccept> {
            override fun onResponse(call: Call<ResponseAccept>, response: Response<ResponseAccept>) {
                if(response.isSuccessful){
                    val responseBody = response.body()

                    Log.d("API RESP", responseBody.toString())

                    // 받아온 데이터에 대한 응답을 처리
                    if(responseBody!=null){
                        val data = responseBody.data
                        resp = data
                        // 원하는 작업을 여기에 추가해 주세요.






                        callback.onSuccess(data)
                    }else{
                        Log.d("API ERROR", "값이 안왔음.")
                        callback.onFailure("값안옴")
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
                            callback.onFailure(e.toString())
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")

                    }
                }
            }
            override fun onFailure(call: Call<ResponseAccept>, t: Throwable) {
                Log.d("API ERROR", t.toString())
                callback.onFailure(t.toString())
            }
        })
    }

    // 가족 구성원 조회
    fun getFamilyMemberInfo(memberId: String, callback:FamilyCallback ) {
        // request
        // refuse는 필요없어서 아무값
        val data = RequestMemberId(memberId)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = familyService.getFamilyMemberInfo(requestBody)
        call.enqueue(object : Callback<ResponseMemberInfo> {
            override fun onResponse(call: Call<ResponseMemberInfo>, response: Response<ResponseMemberInfo>) {
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

                    if (errorBodyString != null) {
                        try {
                            val errorJson = JSONObject(errorBodyString)
                            val code = errorJson.getInt("code")
                            val message = errorJson.getString("message")

                            Log.d("API ERROR", "Error Code: $code, Message: $message")

                        } catch (e: JSONException) {
                            Log.e("API ERROR", "Error parsing JSON: $errorBodyString", e)
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")
                    }
                }
            }
            override fun onFailure(call: Call<ResponseMemberInfo>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }

    //가족 모임장소 조회
    fun getFamilyPlaceInfo(memberId:String, callback:FamilyPlaceCallback){
        // request
        // refuse는 쓸모 없어서 아무값
        val data = RequestMemberId(memberId)
        val json = gson.toJson(data)
        val requestBody = json.toRequestBody(mediaType)

        val call = familyService.getFamilyPlaceInfo(requestBody)
        call.enqueue(object : Callback<ResponsePlaceInfo> {
            override fun onResponse(call: Call<ResponsePlaceInfo>, response: Response<ResponsePlaceInfo>) {
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
                            val code = errorJson.getInt("code")
                            val message = errorJson.getString("message")

                            Log.d("API ERROR", "Error Code: $code, Message: $message")

                        } catch (e: JSONException) {
                            Log.e("API ERROR", "Error parsing JSON: $errorBodyString", e)
                        }
                    } else {
                        Log.d("API ERROR", "Error body is null")
                    }
                }
            }
            override fun onFailure(call: Call<ResponsePlaceInfo>, t: Throwable) {
                Log.d("API ERROR", t.toString())
            }
        })
    }
    interface FamilyRegistrationCallback {
        fun onSuccess(result: String)
        fun onFailure(error: String)
    }

    interface WaitListCallback{
        fun onSuccess(result: ArrayList<WaitInfo>)
        fun onFailure(error:String)
    }

    interface FamilyCallback{
        fun onSuccess(result: ArrayList<FamilyInfo>)
        fun onFailure(error:String)
    }
    interface FamilyPlaceCallback {
        fun onSuccess(result: ArrayList<PlaceInfo>)
        fun onFailure(error:String)
    }
    interface RegistFamilyCallback {
        fun onSuccess(result: PlaceDetailInfo)
        fun onFailure(error:String)
    }

}

