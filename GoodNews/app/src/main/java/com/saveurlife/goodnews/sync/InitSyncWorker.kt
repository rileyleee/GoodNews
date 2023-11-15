package com.saveurlife.goodnews.sync

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.api.MemberAPI
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
        val userDeviceInfoService = UserDeviceInfoService(applicationContext)

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
        var data = memberAPI.findMemberInfo(phoneId)
        // realm 저장
        if(data != null){
            realm.writeBlocking {
                copyToRealm(
                    Member().apply {
                        memberId = data.memberId
                        phone = data.phoneNumber
                        birthDate = data.birthDate
                        name = data.name
                        gender = data.gender
                        bloodType = data.bloodType
                        addInfo = data.addInfo
                        state = data.state
                        lastConnection = RealmInstant.from(newTime/1000, (newTime%1000).toInt())
                        lastUpdate = RealmInstant.from(newTime/1000, (newTime%1000).toInt())
                        latitude = data.lat
                        longitude = data.lon
                        familyId = data.familyId.toInt()
                    }
                )
            }
        }
    }

    // 가족 구성원
    private fun fetchDataFamilyMemInfo() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        familyAPI.getFamilyMemberInfo(phoneId)?.forEach {
            var tempTime = it.lastConnection
            val localDateTime = LocalDateTime.parse(tempTime, formatter)
            val milliseconds =
                localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val temp = memberAPI.findMemberInfo(it.memberId)
            realm.writeBlocking {
                copyToRealm(
                    FamilyMemInfo().apply {
                        id = it.memberId
                        name = it.name
                        phone = it.phoneNumber
                        lastConnection =
                            RealmInstant.from(milliseconds / 1000, (milliseconds % 1000).toInt())
                        state = it.state
                        // 가능하면 server 코드 변경
                        latitude = temp!!.lat
                        longitude = temp!!.lon
                        familyId = it.familyId.toInt()
                    }
                )
            }
        }
    }
    
    // 가족 장소
    private fun fetchDataFamilyPlaceInfo(){
        // 각 장소 placeId 가져와서
        var placeData = familyAPI.getFamilyPlaceInfo(phoneId)

        if(placeData != null){
            placeData.forEach{
                // 각 장소의 자세한 place를 가져온다
                var data = familyAPI.getFamilyPlaceInfoDetail(it.placeId)
                if(data != null){
                    realm.writeBlocking {
                        copyToRealm(
                            FamilyPlace().apply {
                                placeId = it.placeId
                                name = it.name
                                latitude = data.lat
                                longitude = data.lon
                                canUse = it.canuse
                            }
                        )
                    }
                }else{
                    Log.d("Init Sync", "자세한 장소 정보를 받아올 수 없습니다.")
                }
            }
        }else{
            Log.d("Init Sync", "등록된 장소가 없습니다.")
        }
    }

    // 위험 정보
    private fun fetchDataMapInstantInfo() {
        // server 추가 이후 만들어야 함.
        // 위험정보를 모두 가져와서 저장한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        mapAPI.getAllMapFacility()?.forEach {
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
}
