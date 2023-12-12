package com.saveurlife.goodnews.sync

import android.content.Context
import android.util.Log
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
import com.saveurlife.goodnews.api.WaitInfo
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
import kotlin.properties.Delegates

class DataSyncWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){

    private lateinit var realm : Realm
    private lateinit var preferences: PreferencesUtil
    private lateinit var phoneId:String
    private var syncTime by Delegates.notNull<Long>()
    private lateinit var mapAPI: MapAPI
    private lateinit var familyAPI: FamilyAPI
    private lateinit var memberAPI: MemberAPI
    private var newTime by Delegates.notNull<Long>()

    // 다른 곳에서 가져가서 사용할 경우 아래의 코드를 가져가서 실행해주세요!
    /*
        // WorkManager
        private lateinit var workManager:WorkManager
        workManager = WorkManager.getInstance(applicationContext)

        // 조건 설정 - 인터넷 연결 시에만 실행
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // request 생성
        val updateRequest = OneTimeWorkRequest.Builder(DataSyncWorker::class.java)
            .setConstraints(constraints)
            .build()

        // 실행
        workManager.enqueue(updateRequest)
     */


    override fun doWork(): Result {
        // 실행 시 이전 동기화 이후 모든 데이터를 전송한다.
        val userDeviceInfoService = UserDeviceInfoService(applicationContext)


        preferences = GoodNewsApplication.preferences
        phoneId = userDeviceInfoService.deviceId
        syncTime = preferences.getLong("SyncTime",0L)

        mapAPI = MapAPI()
        familyAPI = FamilyAPI()
        memberAPI = MemberAPI()
        realm = Realm.open(GoodNewsApplication.realmConfiguration)

        newTime = System.currentTimeMillis()
//        newTime += TimeUnit.HOURS.toMillis(9)
        try {

            // 1. 회원 가입 정보 -> member table
            fetchDataMember()
            // 2. 가족 구성원 정보 -> familymem_info
            fetchDataFamilyMemInfo()
            // 3. 가족 모임 장소 -> family_place
            fetchDataFamilyPlace()
            // 4. 지도 정보 - 버튼 정보 받기
            fetchDataMapInstantInfo()

            // 5. 시간 정보 갱신
            preferences.setLong("SyncTime", newTime)

        } catch (e : Exception) {
            Log.d("Data Sync", "데이터를 불러오지 못했습니다." +e.toString())
            return Result.failure()
        } finally {
            Log.d("Data Sync", "최신 정보로 업데이트 했습니다.")
            return Result.success()
        }
    }
    // 내 정보
    private fun fetchDataMember(){
        // 현재의 정보를 서버로 보낸다
        val result = realm.query<Member>().first().find()

        if(result!=null){
            var memberId = result.memberId
            var name = result.name
            var gender = result.gender
            var birthDate = result.birthDate
            var bloodType = result.bloodType
            var addInfo = result.addInfo
            var lat = result.latitude
            var lon = result.longitude

            memberAPI.updateMemberInfo(memberId, name, gender, birthDate, bloodType, addInfo, lat, lon)
//            result.lastConnection = RealmInstant.from(newTime/1000, (newTime%1000).toInt())

            realm.writeBlocking {
                val liveObject = this.findLatest(result)
                if (liveObject != null) {
                    liveObject.lastConnection = RealmInstant.from(newTime / 1000, (newTime % 1000).toInt())
                }
            }
        }
    }

    // 가족 구성원 정보
    private fun fetchDataFamilyMemInfo() {
        Log.d("ttest","여기")
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
                result.forEach{
                    var tempTime = it.lastConnection
                    val localDateTime = LocalDateTime.parse(tempTime, formatter)
                    val milliseconds =
                        localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
                    memberAPI.findMemberInfo(it.memberId, object :MemberAPI.MemberCallback{

                        override fun onSuccess(result2: MemberInfo) {
                            realm.writeBlocking {
                                copyToRealm(
                                    FamilyMemInfo().apply {
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

    // 가족 모임 장소
    private fun fetchDataFamilyPlace() {
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
                result.forEach{
                    familyAPI.getFamilyPlaceInfoDetail(it.placeId, object : FamilyAPI.FamilyPlaceDetailCallback{
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


    // 위험 정보 받고 보내기
    private fun fetchDataMapInstantInfo() {
        // 마지막 시간 보다 변경시간이 작을 경우
        // 모두 보내서 반영한다. -> 수정 필요

        val oldData = realm.query<MapInstantInfo>().find()

        val syncService = SyncService()
        if(oldData!=null){
            oldData.forEach {
                if(it.state =="1"){
                    mapAPI.registMapFacility(true, it.content, it.latitude, it.longitude, syncService.realmInstantToString(it.time))
                }else{
                    mapAPI.registMapFacility(false, it.content, it.latitude, it.longitude, syncService.realmInstantToString(it.time))
                }
            }
        }

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
                    val milliseconds = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
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
