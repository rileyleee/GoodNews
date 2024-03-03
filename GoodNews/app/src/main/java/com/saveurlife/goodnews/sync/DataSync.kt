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
import kotlin.properties.Delegates

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

    private lateinit var familyPlaceList:MutableList<PlaceInfo>
    private lateinit var familyPlaceInfoList:MutableList<PlaceDetailInfo>

    private lateinit var newFamilyPlaceList:MutableList<PlaceInfo>
    private lateinit var newFamilyPlaceInfoList:MutableList<PlaceDetailInfo>

    private lateinit var familyInfoList:MutableList<FamilyInfo>
    private lateinit var familyList:MutableList<MemberInfo>

    private lateinit var newFamilyInfoList:MutableList<FamilyInfo>
    private lateinit var newFamilyList:MutableList<MemberInfo>

    private lateinit var alertList: MutableList<Alert>


    private var alertSaveFlag by Delegates.notNull<Int>()

    // 개인 정보 관련

    fun init(){
        alertList = mutableListOf()

        familyPlaceList = mutableListOf()
        familyPlaceInfoList = mutableListOf()

        newFamilyPlaceList = mutableListOf()
        newFamilyPlaceInfoList = mutableListOf()

        familyInfoList = mutableListOf()
        familyList = mutableListOf()

        newFamilyInfoList = mutableListOf()
        newFamilyList = mutableListOf()

        alertSaveFlag = 0
    }

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

    fun saveFamilyMemInfo(familyMemInfoUpdated:MutableLiveData<Boolean>){
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")


        var realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
        var findMem = realm.query<FamilyMemInfo>()

        for(i in 0..< familyInfoList.size) {
            val familyInfo = familyInfoList[i]
            val familyDetail = familyList[i]

            var tempTime = familyInfo.lastConnection
            val localDateTime = LocalDateTime.parse(tempTime, formatter)
            val milliseconds =
                localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

            for (t in findMem.find()) {
                realm.writeBlocking {
                    var res = findLatest(t)
                    if (res != null && res.id == familyInfo.memberId) {
                        res.name = familyInfo.name
                        res.phone = familyInfo.phoneNumber
                        res.lastConnection = RealmInstant.from(
                            milliseconds / 1000,
                            (milliseconds % 1000).toInt()
                        )
                        res.state = familyInfo.state
                        res.latitude = familyDetail!!.lat
                        res.longitude = familyDetail!!.lon
                        res.familyId = familyInfo.familyId
                    }

                }
            }
        }


        for(i in 0..< newFamilyInfoList.size){
            val familyInfo = newFamilyInfoList[i]
            val familyDetail = newFamilyList[i]

            var tempTime = familyInfo.lastConnection

            val localDateTime = LocalDateTime.parse(tempTime, formatter)
            val milliseconds =
                localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()


            var realm = Realm.open(GoodNewsApplication.realmConfiguration)
            realm.writeBlocking {
                copyToRealm(
                    FamilyMemInfo().apply {
                        id = familyInfo.memberId
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
                )
            }
        }
        familyMemInfoUpdated.postValue(true)
    }
    // 가족 관련
    // 1) 가족 구성원 정보
    fun fetchDataFamilyMemInfo(familyMemInfoUpdated:MutableLiveData<Boolean>) {
        // 온라인 일때만 수정 하도록 만들면 될 것 같다.

        // 가족 정보를 받아와 realm을 수정한다.
        // 변경한 경우에만 realm을 수정해야 한다.

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        familyAPI.getFamilyMemberInfo(phoneId, object : FamilyAPI.FamilyCallback {
            override fun onSuccess(result: ArrayList<FamilyInfo>) {
                var cnt = 0
                result.forEach {
                    val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
                    var data = realm.query<FamilyMemInfo>("id == $0", it.memberId).first().find()

                    var findMem:FamilyMemInfo? = data?.let {
                        var state = "";
                        if(it.state != null){
                            state = it.state!!
                        }
                        FamilyMemInfo(
                            id = it.id,
                            name = it.name,
                            phone =it.phone,
                            lastConnection = it.lastConnection,
                            state = state,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            familyId  = it.familyId
                        )
                    }
                    realm.close()


                    if(findMem != null){
                        // 이미 가족이 연결이 되었던 경우 -> 변경
                        memberAPI.findMemberInfo(it.memberId, object : MemberAPI.MemberCallback {
                            override fun onSuccess(result2: MemberInfo) {
                                familyInfoList.add(it)
                                familyList.add(result2)
                                cnt++
                                if(cnt == result.size){
                                    saveFamilyMemInfo(familyMemInfoUpdated)
                                    alertSaveFlag += 1
                                    if(alertSaveFlag == 2){
                                        saveAlertList()
                                    }
                                }
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


                                // 여기 들어갈 정보는 나중에 바꿔야 할듯!
                                var tempTime = it.lastConnection
                                val localDateTime = LocalDateTime.parse(tempTime, formatter)
                                val milliseconds = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

                                alertList.add(
                                    Alert(
                                        id = "FF${result2.memberId}",
                                        senderId = "",
                                        name = result2.name,
                                        content = result2.name,
                                        latitude = 0.0,
                                        longitude = 0.0,
                                        time = timeService.convertLongToRealmInstant(milliseconds),
                                        type = "멤버"
                                    )
                                )
                                cnt++

                                if(cnt == result.size){
                                    saveFamilyMemInfo(familyMemInfoUpdated)
                                    alertSaveFlag += 1
                                    if(alertSaveFlag == 2){
                                        saveAlertList()
                                    }
                                }
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
    }

    fun saveAlertList(){
        Log.d("동기화 알림 저장", alertList.size.toString()+"개의 알림이 저장되었습니다.")
        alertList.forEach {
            var realm = Realm.open(GoodNewsApplication.realmConfiguration)
            realm.writeBlocking {
                copyToRealm(
                    Alert().apply {
                        id = it.id
                        senderId = it.senderId
                        name = it.name
                        content = it.content
                        latitude = it.latitude
                        longitude = it.longitude
                        time = it.time
                        type = it.type
                    }
                )
            }
        }
    }

    fun saveFamilyPlace(familyPlaceUpdated:MutableLiveData<Boolean>){
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        var realm = Realm.open(GoodNewsApplication.realmConfiguration)


        var findPlace = realm.query<FamilyPlace>()
        for(i in 0..<familyPlaceList.size){
            val familyPlace = familyPlaceList[i]
            val familyPlaceInfo = familyPlaceInfoList[i]

            for(t in findPlace.find()){
                realm.writeBlocking {
                    var res = findLatest(t)

                    if(res!=null && res.placeId == familyPlace.placeId){
                            res.name = familyPlaceInfo.name
                            res.address = familyPlaceInfo.address
                            res.latitude = familyPlaceInfo.lat
                            res.longitude = familyPlaceInfo.lon
                            res.canUse = familyPlaceInfo.canuse
                    }
                }
            }
        }
        for(i in 0..<newFamilyPlaceInfoList.size){
            val familyPlace = newFamilyPlaceList[i]
            val familyPlaceInfo = newFamilyPlaceInfoList[i]

            var tempTime = familyPlace.createdDate
            val localDateTime = LocalDateTime.parse(tempTime, formatter)
            val milliseconds = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

            var realm = Realm.open(GoodNewsApplication.realmConfiguration)
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
            }
        }

        familyPlaceUpdated.postValue(true)
        preferences.setLong("FamilySyncTime", newTime)
    }

    // 가족 모임 장소
    fun fetchDataFamilyPlace(familyPlaceUpdated:MutableLiveData<Boolean>) {
        // 장소의 새로운 상태를 받아온다
        // realm에 저장한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")



        familyAPI.getFamilyPlaceInfo(phoneId, object : FamilyAPI.FamilyPlaceCallback {
            override fun onSuccess(result: ArrayList<PlaceInfo>) {
                var cnt = 0
                result.forEach {
                    // id로 찾았을 때 없으면 -> 그냥 추가
                    // 만약,시간이 같지 않다면 -> 추가

                    // 시간이 같다면 그냥 패스
                    val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
                    var data = realm.query<FamilyPlace>("placeId == $0", it.placeId).first().find()
                    var findPlace:FamilyPlace? = data?.let {
                        FamilyPlace(
                            placeId = it.placeId,
                            name = it.name,
                            address = it.address,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            canUse = it.canUse,
                            seq = it.seq,
                            lastUpdate = it.lastUpdate
                        )
                    }
                    realm.close()


                    if(findPlace!= null){
                        val serverUpdateTime = timeService.convertDateStrToLong(it.createdDate)
                        var lastUpdateTime = timeService.realmInstantToLong(findPlace.lastUpdate)

                        if(serverUpdateTime != lastUpdateTime){
                            // 변경이 필요한 경우
                            familyAPI.getFamilyPlaceInfoDetail(
                                it.placeId,
                                object : FamilyAPI.FamilyPlaceDetailCallback {
                                    override fun onSuccess(result2: PlaceDetailInfo) {
                                        familyPlaceList.add(it)
                                        familyPlaceInfoList.add(result2)
                                        cnt++

                                        var tempTime = it.createdDate
                                        val localDateTime = LocalDateTime.parse(tempTime, formatter)
                                        val milliseconds = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()

                                        var alertCheck = false
                                        var alertMsg = "${findPlace.seq}/"

                                        if ((findPlace.address != result2.address) || (findPlace.name != result2.name)) {
                                            // 주소가 변경
                                            alertMsg += "주소/"
                                            alertCheck = true
                                        } else {
                                            alertMsg += "-/"
                                        }

                                        if (findPlace.canUse != result2.canuse) {
                                            // 위험 정보가 변경
                                            alertCheck = true
                                            if (result2.canuse) {
                                                alertMsg += "안전"
                                            } else {
                                                alertMsg += "위험"
                                            }
                                        } else {
                                            alertMsg += "-"
                                        }

                                        if(alertCheck){
                                            alertList.add(
                                                Alert(
                                                    id = "P${findPlace.seq}/${timeService.convertLongToStr(newTime)}",
                                                    senderId = "",
                                                    name = result2.registerUser,
                                                    content = alertMsg,
                                                    latitude = 0.0,
                                                    longitude = 0.0,
                                                    time = timeService.convertLongToRealmInstant(milliseconds),
                                                    type = "장소"
                                                )
                                            )
                                        }

                                        if(cnt == result.size){
                                            saveFamilyPlace(familyPlaceUpdated)
                                            alertSaveFlag += 1
                                            if(alertSaveFlag == 2){
                                                saveAlertList()
                                            }
                                        }
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
                                    cnt++
                                    if(cnt == result.size){
                                        saveFamilyPlace(familyPlaceUpdated)
                                        alertSaveFlag += 1
                                        if(alertSaveFlag == 2) {
                                            saveAlertList()
                                        }
                                    }
                                }

                                override fun onFailure(error: String) {
                                    Log.e(TAG_ERR, "FamilyAPI Error : $error")
                                }
                            }
                        )
                    }
                }
            }
            override fun onFailure(error: String) {
                Log.e(TAG_ERR, "FamilyAPI Error : $error")
            }
        })
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
        realm.close()
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
                    val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
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
                    realm.close()
                    // 시간 변경
                    preferences.setLong("FacilitySyncTime", newTime)
                }

                override fun onFailure(error: String) {
                    Log.e(TAG_ERR, "Facility Error : $error" )
                }
            }
        )

    }
}