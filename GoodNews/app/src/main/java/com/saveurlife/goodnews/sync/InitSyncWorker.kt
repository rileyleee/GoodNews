package com.saveurlife.goodnews.sync

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.api.FacilityState
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.FamilyInfo
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
import io.realm.kotlin.types.RealmInstant
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class InitSyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private lateinit var realm : Realm
    private lateinit var preferences: PreferencesUtil
    private lateinit var phoneId:String
    private var syncTime by Delegates.notNull<Long>()
    private lateinit var mapAPI:MapAPI
    private lateinit var familyAPI: FamilyAPI
    private lateinit var memberAPI: MemberAPI
    private var newTime by Delegates.notNull<Long>()


    // 다른 곳에서 가져가서 사용할 경우 아래의 코드를 가져가서 실행해주세요!
    // 서버 연결 시에만 요청하도록 추가해주세요!

    /*
        // WorkManager
        private lateinit var workManager:WorkManager
        workManager = WorkManager.getInstance(applicationContext)

        // 조건 설정 - 인터넷 연결 시에만 실행
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // request 생성
        val updateRequest = OneTimeWorkRequest.Builder(InitSyncWorker::class.java)
            .setConstraints(constraints)
            .build()

        // 실행
        workManager.enqueue(updateRequest)
     */

    override fun doWork(): Result {
        // 초기에 기존 정보를 가져 오고자 할 경우 모든 정보를 realm에 저장한다.
        val userDeviceInfoService = UserDeviceInfoService.getInstance(applicationContext)

        // 기기 id를 가져온다
        realm = Realm.open(GoodNewsApplication.realmConfiguration)
        preferences = GoodNewsApplication.preferences
        phoneId = userDeviceInfoService.deviceId
        syncTime = preferences.getLong("SyncTime",0L)

        newTime = System.currentTimeMillis()
        newTime += TimeUnit.HOURS.toMillis(9)

        mapAPI = MapAPI()
        familyAPI = FamilyAPI()
        memberAPI = MemberAPI()

        try{
            // 회원
            fetchDataMember()
            // 가족 구성원
            fetchDataFamilyMemInfo()
            // 가족 모임 장소
            fetchDataFamilyPlaceInfo()
            // 버튼 정보 - server 변경 후 반영
            fetchDataMapInstantInfo()
            // 대피소 상황 정보 - server 변경 후 반영

        } catch (e: Exception){
            Log.d("Init Sync", "Sync 과정에서 오류가 생겼습니다."+ e.toString())
            Toast.makeText(
                applicationContext,
                "정보를 받아올 수 없습니다. 다시 시도해 주세요.",
                Toast.LENGTH_LONG
            )
            return Result.failure()
        } finally {
            // 마지막 동기화 시간 반영
            preferences.setLong("SyncTime", newTime)
            Log.d("Init Sync", "정보를 받아왔습니다.")

            Toast.makeText(
                applicationContext,
                "정보를 받아왔습니다.",
                Toast.LENGTH_LONG
            )
            return Result.success()
        }

    }

    // 회원
    private fun fetchDataMember(){
        memberAPI.findMemberInfo(phoneId, object :MemberAPI.MemberCallback{
            override fun onSuccess(result: MemberInfo) {
                realm.writeBlocking {
                    copyToRealm(Member().apply {
                        memberId = result.memberId
                        phone = result.phoneNumber
                        birthDate = result.birthDate
                        name = result.name
                        gender = result.gender
                        bloodType = result.bloodType
                        addInfo = result.addInfo
                        state = result.state
                        lastConnection = RealmInstant.from(newTime/1000, (newTime%1000).toInt())
                        lastUpdate = RealmInstant.from(newTime/1000, (newTime%1000).toInt())
                        latitude = result.lat
                        longitude = result.lon
                        familyId = result.familyId
                    })
                }

            }

            override fun onFailure(error: String) {

            }

        })
    }

    // 가족 구성원
    private fun fetchDataFamilyMemInfo() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        familyAPI.getFamilyMemberInfo(phoneId, object : FamilyAPI.FamilyCallback {
            override fun onSuccess(result: ArrayList<FamilyInfo>) {
                result.forEach{
                    var tempTime = it.lastConnection
                    val localDateTime = LocalDateTime.parse(tempTime, formatter)
                    val milliseconds =
                        localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    memberAPI.findMemberInfo(it.memberId, object :MemberAPI.MemberCallback{

                        override fun onSuccess(result2: MemberInfo) {
                            realm.writeBlocking {
                                copyToRealm(FamilyMemInfo().apply {
                                    id = it.memberId
                                    name = it.name
                                    phone = it.phoneNumber
                                    lastConnection = RealmInstant.from(milliseconds / 1000, (milliseconds % 1000).toInt())
                                    state = it.state
                                    latitude = result2!!.lat
                                    longitude = result2!!.lon
                                    familyId = it.familyId
                                })
                            }

                        }

                        override fun onFailure(error: String) {

                        }

                    })
                }
            }
            override fun onFailure(error: String) {
                // 실패 시의 처리
                Log.d("Family", "Registration failed: $error")
            }
        })
    }

    // 가족 장소
    private fun fetchDataFamilyPlaceInfo(){
        familyAPI.getFamilyPlaceInfo(phoneId, object : FamilyAPI.FamilyPlaceCallback {
            override fun onSuccess(result: ArrayList<PlaceInfo>) {
                result.forEach{
                    familyAPI.getFamilyPlaceInfoDetail(it.placeId, object : FamilyAPI.FamilyPlaceDetailCallback{
                        override fun onSuccess(result2: PlaceDetailInfo) {
                            realm.writeBlocking {
                                copyToRealm(
                                    FamilyPlace().apply {
                                        placeId = result2.placeId
                                        name = result2.name
                                        latitude = result2.lat
                                        longitude = result2.lon
                                        canUse = result2.canuse
                                    }
                                )
                            }
                        }
                        override fun onFailure(error: String) {

                        }
                    })
                }
            }
            override fun onFailure(error: String) {

            }
        })
    }

    // 위험 정보
    private fun fetchDataMapInstantInfo() {
        // server 추가 이후 만들어야 함.
        // 위험정보를 모두 가져와서 저장한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        mapAPI.getAllMapFacility(object : MapAPI.FacilityStateCallback{
            override fun onSuccess(result: ArrayList<FacilityState>) {
                result.forEach {
                    var tempState:String = ""
                    if(it.buttonType){
                        tempState = "1"
                    }else{
                        tempState = "0"
                    }
                    val localDateTime = LocalDateTime.parse(it.lastModifiedDate, formatter)
                    val milliseconds = localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    realm.writeBlocking {
                        copyToRealm(
                            MapInstantInfo().apply {
                                state = tempState
                                content = it.text
                                time = RealmInstant.from(milliseconds/1000, (milliseconds%1000).toInt())
                                latitude = it.lat
                                longitude = it.lon
                            }
                        )
                    }
                }
            }

            override fun onFailure(error: String) {
            }
        })
    }
}
