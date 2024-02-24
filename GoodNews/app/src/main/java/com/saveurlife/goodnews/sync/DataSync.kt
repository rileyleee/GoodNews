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
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.UserDeviceInfoService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DataSync (context: Context) {
    private val userDeviceInfoService: UserDeviceInfoService =
        UserDeviceInfoService.getInstance(context)
    private val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
    private val preferences: PreferencesUtil = GoodNewsApplication.preferences
    private val phoneId: String = userDeviceInfoService.deviceId
    private val phoneNumber : String = userDeviceInfoService.phoneNumber

    private val syncTime = preferences.getLong("SyncTime", 0L)
    private val familySyncTime = preferences.getLong("FamilySyncTime", 0L)
    private val facilitySyncTime = preferences.getLong("FacilitySyncTime", 0L)

    private val TAG_ERR = "SYNC ERROR"

    private val mapAPI: MapAPI = MapAPI()
    private val familyAPI: FamilyAPI = FamilyAPI()
    private val memberAPI: MemberAPI = MemberAPI()

    private val newTime = System.currentTimeMillis()


//    val familyMemInfoUpdated = MutableLiveData<Boolean>()
//    val familyPlaceUpdated = MutableLiveData<Boolean>()



    // 개인 정보 관련

    fun fetchDataMember() {
        // 현재의 정보를 서버로 보낸다
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
    }


    // 가족 관련
    // 1) 가족 구성원 정보
    fun fetchDataFamilyMemInfo(familyMemInfoUpdated:MutableLiveData<Boolean>) {
        // 온라인 일때만 수정 하도록 만들면 될 것 같다.

        GlobalScope.launch {
            realm.writeBlocking {
                query<FamilyMemInfo>().find()
                    ?.also { delete(it) }
            }
        }
        // 가족 정보를 받아와 realm을 수정한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        familyAPI.getFamilyMemberInfo(phoneId, object : FamilyAPI.FamilyCallback {
            override fun onSuccess(result: ArrayList<FamilyInfo>) {
                result.forEach {
                    var tempTime = it.lastConnection
                    val localDateTime = LocalDateTime.parse(tempTime, formatter)
                    val milliseconds =
                        localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
                    memberAPI.findMemberInfo(it.memberId, object : MemberAPI.MemberCallback {

                        override fun onSuccess(result2: MemberInfo) {
                            realm.writeBlocking {
                                copyToRealm(
                                    FamilyMemInfo().apply {
                                        id = it.memberId
                                        name = it.name
                                        phone = it.phoneNumber
                                        lastConnection = RealmInstant.from(
                                            milliseconds / 1000,
                                            (milliseconds % 1000).toInt()
                                        )
                                        state = it.state
                                        latitude = result2!!.lat
                                        longitude = result2!!.lon
                                        familyId = it.familyId
                                    })
                            }
                            familyMemInfoUpdated.postValue(true)
                        }

                        override fun onFailure(error: String) {
                            Log.e(TAG_ERR, "Family Sync Error : $error")
                        }
                    })
                }
            }

            override fun onFailure(error: String) {
                // 실패 시의 처리
                Log.e(TAG_ERR, "Registration Failed: $error")
            }
        })
    }

    // 가족 모임 장소
    fun fetchDataFamilyPlace(familyPlaceUpdated:MutableLiveData<Boolean>) {
        // 내가 변경한 장소 수정
        val allData = realm.query<FamilyPlace>().find()
        allData.forEach {
            familyAPI.getFamilyUpdatePlaceCanUse(it.placeId, it.canUse)
            familyAPI.getFamilyUpdatePlaceInfo(
                it.placeId,
                it.name,
                it.latitude,
                it.longitude
            )
        }

        // 장소의 새로운 상태를 받아온다
        // 어짜피 3개 밖에 없으므로 다 삭제후 넣는다.

        GlobalScope.launch {
            realm.writeBlocking {
                query<FamilyPlace>().find()
                    ?.also { delete(it) }
            }
        }

        // realm에 저장한다.
        familyAPI.getFamilyPlaceInfo(phoneId, object : FamilyAPI.FamilyPlaceCallback {
            override fun onSuccess(result: ArrayList<PlaceInfo>) {
                result.forEach {
                    familyAPI.getFamilyPlaceInfoDetail(
                        it.placeId,
                        object : FamilyAPI.FamilyPlaceDetailCallback {
                            override fun onSuccess(result2: PlaceDetailInfo) {
                                realm.writeBlocking {
                                    copyToRealm(
                                        FamilyPlace().apply {
                                            placeId = result2.placeId
                                            name = result2.name
                                            address = result2.address
                                            latitude = result2.lat
                                            longitude = result2.lon
                                            canUse = result2.canuse
                                            seq = it.seq
                                        }
                                    )
                                }
                                familyPlaceUpdated.postValue(true)
                                // family
                                preferences.setLong("FamilySyncTime", newTime)
                            }

                            override fun onFailure(error: String) {
                                Log.e(TAG_ERR, "FamilyAPI Error : $error")
                            }
                        }
                    )
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
        val timeService = TimeService()

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
            })
    }
}