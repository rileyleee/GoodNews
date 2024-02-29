package com.saveurlife.goodnews.sync

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.api.DurationFacilityState
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.FamilyInfo
import com.saveurlife.goodnews.api.FirstLogin
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.api.MemberAPI
import com.saveurlife.goodnews.api.MemberInfo
import com.saveurlife.goodnews.api.PlaceDetailInfo
import com.saveurlife.goodnews.api.PlaceInfo
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.models.Alert
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.UserDeviceInfoService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DataSync (context: Context) {
    private val userDeviceInfoService: UserDeviceInfoService =
        UserDeviceInfoService.getInstance(context)
    private val preferences: PreferencesUtil = GoodNewsApplication.preferences
    private val phoneId: String = userDeviceInfoService.deviceId
    private val phoneNumber : String = userDeviceInfoService.phoneNumber

    private val syncTime = preferences.getLong("SyncTime", 0L)
    private val familySyncTime = preferences.getLong("FamilySyncTime", 0L)
    private val facilitySyncTime = preferences.getLong("FacilitySyncTime", 0L)
    private val myName = preferences.getString("name", "가족")

    private val TAG_ERR = "SYNC ERROR"

    private val mapAPI: MapAPI = MapAPI()
    private val familyAPI: FamilyAPI = FamilyAPI()
    private val memberAPI: MemberAPI = MemberAPI()

    private val newTime = System.currentTimeMillis()
    private val timeService = TimeService();



    // 개인 정보 관련

    fun fetchDataMember() {
        // 현재의 정보를 서버로 보낸다
        val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val result = realm.query<Member>().first().find()

        if (result != null) {
            var memberId = result.memberId
            var name = result.name
            var gender = result.gender
            var birthDate = result.birthDate
            var bloodType = result.bloodType
            var addInfo = result.addInfo
            var lat = result.latitude
            var lon = result.longitude

            memberAPI.firstLoginInfo(memberId, object : MemberAPI.LoginCallback {
                override fun onSuccess(result: FirstLogin) {
                    if (result.firstLogin) {
                        // 기존 업데이트
                        memberAPI.updateMemberInfo(
                            memberId,
                            name,
                            gender,
                            birthDate,
                            bloodType,
                            addInfo,
                            lat,
                            lon
                        )
                    } else {
                        // 처음 등록
                        memberAPI.registMemberInfo(
                            memberId,
                            phoneNumber,
                            name,
                            birthDate,
                            gender,
                            bloodType,
                            addInfo
                        )
                        memberAPI.updateMember(memberId, lat, lon)
                    }
                }

                override fun onFailure(error: String) {
                    Log.e(TAG_ERR, "개인 정보 등록 오류 : $error")
                }
            })

            preferences.setLong("SyncTime", newTime)
            realm.writeBlocking {
                val liveObject = this.findLatest(result)
                if (liveObject != null) {
                    liveObject.lastConnection =
                        RealmInstant.from(newTime / 1000, (newTime % 1000).toInt())
                }
            }
        }
        realm.close()
    }


    // 가족 관련
    // 1) 가족 구성원 정보
    fun fetchDataFamilyMemInfo(familyMemInfoUpdated:MutableLiveData<Boolean>) {
        // 온라인 일때만 수정 하도록 만들면 될 것 같다.

        // 가족 정보를 받아와 realm을 수정한다.
        // 변경한 경우에만 realm을 수정해야 한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        var familyInfoList:MutableList<FamilyInfo> = mutableListOf()
        var familyList:MutableList<MemberInfo> = mutableListOf()

        var newFamilyInfoList:MutableList<FamilyInfo> = mutableListOf()
        var newFamilyList:MutableList<MemberInfo> = mutableListOf()

        familyAPI.getFamilyMemberInfo(phoneId, object : FamilyAPI.FamilyCallback {
            override fun onSuccess(result: ArrayList<FamilyInfo>) {
                result.forEach {
                    val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
                    var findMem = realm.query<FamilyMemInfo>("id == $0", it.memberId).find()

                    if(findMem.size != 0){
                        // 이미 가족이 연결이 되었던 경우 -> 변경
                        memberAPI.findMemberInfo(it.memberId, object : MemberAPI.MemberCallback {
                            override fun onSuccess(result2: MemberInfo) {
                                familyInfoList.add(it)
                                familyList.add(result2)
                            }
                            override fun onFailure(error: String) {
                                Log.e(TAG_ERR, "Family Sync Error : $error")
                            }
                        })
                    }else{
                        // 가족이 연결되지 않았을 경우 -> 새로 추가
                        memberAPI.findMemberInfo(it.memberId, object : MemberAPI.MemberCallback {
                            override fun onSuccess(result2: MemberInfo) {
                                newFamilyInfoList.add(it)
                                newFamilyList.add(result2)
                            }
                            override fun onFailure(error: String) {
                                Log.e(TAG_ERR, "Family Sync Error : $error")
                            }
                        })
                    }
                    realm.close()
                }
            }
            override fun onFailure(error: String) {
                // 실패 시의 처리
                Log.e(TAG_ERR, "Registration Failed: $error")
            }
        })

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        realm.writeBlocking {
            for(i in 0..< familyInfoList.size){
                val familyInfo = familyInfoList[i]
                val familyDetail = familyList[i]
                var findMem = realm.query<FamilyMemInfo>("id == $0", familyInfo.memberId).find().first()
                                    var tempTime = familyInfo.lastConnection
                    val localDateTime = LocalDateTime.parse(tempTime, formatter)
                    val milliseconds =
                        localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
                findMem.apply {
                    name = familyInfo.name
                    phone = familyInfo.phoneNumber
                    lastConnection = RealmInstant.from(
                                                milliseconds / 1000,
                                                (milliseconds % 1000).toInt()
                    )
                    state = familyInfo.state
                    latitude = familyDetail!!.lat
                    longitude = familyDetail!!.lon
                    familyId = familyInfo.familyId
                }
            }

            for(i in 0..< newFamilyInfoList.size){
                val familyInfo = newFamilyInfoList[i]
                val familyDetail = newFamilyList[i]

                var findMem = realm.query<FamilyMemInfo>("id == $0", familyInfo.memberId).find().first()
                var tempTime = familyInfo.lastConnection

                val localDateTime = LocalDateTime.parse(tempTime, formatter)
                val milliseconds =
                    localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

                findMem.apply {
                    name = familyInfo.name
                    phone = familyInfo.phoneNumber
                    lastConnection = RealmInstant.from(
                        milliseconds / 1000,
                        (milliseconds % 1000).toInt()
                    )
                    state = familyInfo.state
                    latitude = familyDetail!!.lat
                    longitude = familyDetail!!.lon
                    familyId = familyInfo.familyId
                }

                // 새로운 가족 추가 알림이 필요한 경우 사용
                copyToRealm(
                    Alert().apply {
                        id ="FF${familyInfo.memberId}"
                        name = ""
                        content = familyInfo.name
                        time = timeService.convertLongToRealmInstant(milliseconds)
                        type = "멤버"
                    }
                )
            }
        }
        familyMemInfoUpdated.postValue(true)

    }

    // 가족 모임 장소
    fun fetchDataFamilyPlace(familyPlaceUpdated:MutableLiveData<Boolean>) {
        // 장소의 새로운 상태를 받아온다
        // realm에 저장한다.
        var familyPlaceList:MutableList<PlaceInfo> = mutableListOf()
        var familyPlaceInfoList:MutableList<PlaceDetailInfo> = mutableListOf()

        var newFamilyPlaceList:MutableList<PlaceInfo> = mutableListOf()
        var newFamilyPlaceInfoList:MutableList<PlaceDetailInfo> = mutableListOf()

        familyAPI.getFamilyPlaceInfo(phoneId, object : FamilyAPI.FamilyPlaceCallback {
            override fun onSuccess(result: ArrayList<PlaceInfo>) {
                val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
                result.forEach {
                    // id로 찾았을 때 없으면 -> 그냥 추가
                    // 만약,시간이 같지 않다면 -> 추가

                    // 시간이 같다면 그냥 패스
                    var findPlace = realm.query<FamilyPlace>("placeId == $0", it.placeId).find()

                    if(findPlace.size != 0){
                        var res = findPlace.first()
                        val serverUpdateTime = timeService.convertDateStrToLong(it.createdDate)
                        var lastUpdateTime = timeService.realmInstantToLong(res.lastUpdate)

                        if(serverUpdateTime != lastUpdateTime){
                            // 변경이 필요한 경우
                            familyAPI.getFamilyPlaceInfoDetail(
                                it.placeId,
                                object : FamilyAPI.FamilyPlaceDetailCallback {
                                    override fun onSuccess(result2: PlaceDetailInfo) {
                                        familyPlaceList.add(it)
                                        familyPlaceInfoList.add(result2)
                                    }
                                    override fun onFailure(error: String) {
                                        Log.e(TAG_ERR, "FamilyAPI Error : $error")
                                    }
                                }
                            )
                        }
                    }else{
                        // 새로 등록하는 경우
                        familyAPI.getFamilyPlaceInfoDetail(
                            it.placeId,
                            object : FamilyAPI.FamilyPlaceDetailCallback {
                                override fun onSuccess(result2: PlaceDetailInfo) {
                                    newFamilyPlaceList.add(it)
                                    newFamilyPlaceInfoList.add(result2)
                                }

                                override fun onFailure(error: String) {
                                    Log.e(TAG_ERR, "FamilyAPI Error : $error")
                                }
                            }
                        )
                    }
                }
                realm.close()
            }
            override fun onFailure(error: String) {
                Log.e(TAG_ERR, "FamilyAPI Error : $error")
            }
        })

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        realm.writeBlocking {
            for(i in 0..<familyPlaceList.size){
                val familyPlace = familyPlaceList[i]
                val familyPlaceInfo = familyPlaceInfoList[i]

                var findPlace = realm.query<FamilyPlace>("placeId == $0", familyPlace.placeId).find().first()

                var tempTime = familyPlace.createdDate
                val localDateTime = LocalDateTime.parse(tempTime, formatter)
                val milliseconds = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

                var alertCheck = false
                var alertMsg = "${familyPlace.seq}/"


                if((familyPlaceInfo.address != findPlace.address) || (familyPlaceInfo.name != findPlace.name)){
                                            // 주소가 변경
                    alertMsg += "주소/"
                    alertCheck = true
                }else{
                    alertMsg += "-/"
                }
                if(familyPlaceInfo.canuse != findPlace.canUse) {
                                            // 위험 정보가 변경
                    alertCheck = true
                    if(findPlace.canUse){
                        alertMsg += "안전"
                    }else{
                        alertMsg += "위험"
                    }
                }else{
                    alertMsg += "-"
                }


                findPlace.apply {
                    name = familyPlaceInfo.name
                    address = familyPlaceInfo.address
                    latitude = familyPlaceInfo.lat
                    longitude = familyPlaceInfo.lon
                    canUse = familyPlaceInfo.canuse
                }
                if(alertCheck){
                     copyToRealm(
                        Alert().apply {
                            id ="P${findPlace.seq}/${timeService.convertLongToStr(newTime)}"
                            name = familyPlaceInfo.registerUser
                            content = alertMsg
                            time = timeService.convertLongToRealmInstant(milliseconds)
                            type = "장소"
                        }
                    )
                }
            }
            for(i in 0..<newFamilyPlaceInfoList.size){
                val familyPlace = newFamilyPlaceList[i]
                val familyPlaceInfo = newFamilyPlaceInfoList[i]

                var tempTime = familyPlace.createdDate
                val localDateTime = LocalDateTime.parse(tempTime, formatter)
                val milliseconds = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

                realm.writeBlocking {
                    copyToRealm(
                        FamilyPlace().apply {
                            placeId = familyPlaceInfo.placeId
                            name = familyPlaceInfo.name
                            address = familyPlaceInfo.address
                            latitude = familyPlaceInfo.lat
                            longitude = familyPlaceInfo.lon
                            canUse = familyPlaceInfo.canuse
                            seq = familyPlace.seq
                            lastUpdate = timeService.convertLongToRealmInstant(milliseconds)
                        }
                    )

                    copyToRealm(
                        Alert().apply {
                            id ="P${familyPlace.seq}/${timeService.convertLongToStr(newTime)}"
                            name = familyPlaceInfo.registerUser
                            content = "등록"
                            time = timeService.convertLongToRealmInstant(milliseconds)
                            type = "장소"
                        }
                    )
                }
            }
        }
        familyPlaceUpdated.postValue(true)
        preferences.setLong("FamilySyncTime", newTime)
    }

    // 위험 정보
    // 위험 정보 받고 보내기
    fun fetchDataMapInstantInfo() {
        // 마지막 시간 보다 변경시간이 작을 경우
        // 모두 보내서 반영한다. -> 수정 필요
        val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val oldData = realm.query<MapInstantInfo>().find()
        // 시간 기준 필터링
        val result: List<MapInstantInfo> =
            oldData.filter { it.time > timeService.convertLongToRealmInstant(syncTime) }

        // 마지막 연결 시각 이후 변경된 리스트 불러오기

        if (result != null) {
            result.forEach {
                if (it.state == "1") {
                    mapAPI.registMapFacility(
                        it.id,
                        true,
                        it.content,
                        it.latitude,
                        it.longitude,
                        timeService.realmInstantToString(it.time)
                    )
                } else {
                    mapAPI.registMapFacility(
                        it.id,
                        false,
                        it.content,
                        it.latitude,
                        it.longitude,
                        timeService.realmInstantToString(it.time)
                    )
                }
            }
        }

        // 위험정보를 모두 가져와서 저장한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val startTime1 = newTime - TimeUnit.DAYS.toMillis(7)
        val startTime2 = facilitySyncTime
        // 일주일 전 또는 마지막 변경일 이후 데이터
        val startTime = startTime1.coerceAtLeast(startTime2)

        mapAPI.getDurationFacility(
            timeService.convertDateLongToString(startTime),
            object : MapAPI.FacilityStateDurationCallback {
                override fun onSuccess(result: ArrayList<DurationFacilityState>) {
                    result.forEach {
                        val vaildCheck = realm.query<MapInstantInfo>("id == $0", it.id).find()
                        if(vaildCheck == null) {
                            var stateStr = ""
                            if (it.buttonType) {
                                stateStr = "1"
                            } else {
                                stateStr = "0"
                            }
                            val localDateTime = LocalDateTime.parse(it.createdDate, formatter)
                            val milliseconds =
                                localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

                            realm.writeBlocking {
                                copyToRealm(
                                    MapInstantInfo().apply {
                                        id = it.id
                                        state = stateStr
                                        content = it.text
                                        time = RealmInstant.from(
                                            milliseconds / 1000,
                                            (milliseconds % 1000).toInt()
                                        )
                                        latitude = it.lat
                                        longitude = it.lon

                                    }
                                )
                            }
                        }
                    }

                    // 시간 변경
                    preferences.setLong("FacilitySyncTime", newTime)
                }

                override fun onFailure(error: String) {
                    Log.e(TAG_ERR, "Facility Error : $error" )
                }
            }
        )
        realm.close()
    }
}