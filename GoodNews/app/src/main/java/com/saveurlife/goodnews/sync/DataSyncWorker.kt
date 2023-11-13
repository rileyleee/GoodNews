package com.saveurlife.goodnews.sync

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.api.MemberAPI
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.Location
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

        realm = Realm.open(GoodNewsApplication.realmConfiguration)
        preferences = PreferencesUtil(applicationContext)
        phoneId = "테스트" // 값 바꿔 두기
        syncTime = preferences.getLong("SyncTime",0L)

        mapAPI = MapAPI()
        familyAPI = FamilyAPI()
        memberAPI = MemberAPI()

        newTime = System.currentTimeMillis()
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
            Log.d("Data Sync", "데이터를 불러오지 못했습니다.")
             return Result.success()
        } finally {
            Log.d("Data Sync", "최신 정보로 업데이트 했습니다.")
            return Result.failure()
        }
    }

    private fun fetchDataTimeStamp() {

        // realm에 시간 갱신
        realm.writeBlocking {
            copyToRealm(
                // Member
                Member().apply {
                    lastConnection = RealmInstant.from(newTime/1000, (newTime%1000).toInt())
                }
            )
        }
        
    }

    // 내 정보
    private fun fetchDataMember(){
        // 현재의 정보를 서버로 보낸다
        val result = realm.query<Member>().first().find()

        if(result!=null){
            var memberId = result.memberId
            var name = result.name
//            var gender = result.gender -> server 수정 필요
            var birthDate = result.birthDate
            var bloodType = result.bloodType
            var addInfo = result.addInfo

            memberAPI.updateMemberInfo(memberId, name, birthDate,bloodType, addInfo)
            result.lastConnection = RealmInstant.from(newTime/1000, (newTime%1000).toInt())

        }

    }

    // 가족 구성원 정보
    private fun fetchDataFamilyMemInfo() {
        // 온라인 일때만 수정 하도록 만들면 될 것 같다.

        // 우선 realm 비운다
        val oldData = realm.query<FamilyMemInfo>().find()
        oldData.forEach{
            realm.writeBlocking {
                delete(it)
            }
        }
        // 가족 정보를 받아와 realm을 수정한다.
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        familyAPI.getFamilyMemberInfo(phoneId)?.forEach {
            var tempTime = it.lastConnection
            val localDateTime = LocalDateTime.parse(tempTime, formatter)
            val milliseconds =
                localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            realm.writeBlocking {
                    FamilyMemInfo().apply {
                        id = it.memberId
                        name = it.name
                        // 휴대폰 번호는 server 추가 필요
                        lastConnection = RealmInstant.from(milliseconds / 1000, (milliseconds % 1000).toInt())
                        // state는 server 추가 후 반영
                        // location은 server 추가 후 반영
                        // familyId는 server 추가 후 변영
                    }
            }
        }
    }

    // 가족 모임 장소
    private fun fetchDataFamilyPlace() {
        // 내가 변경한 장소 수정
        val allData = realm.query<FamilyPlace>().find()
        allData.forEach {
            familyAPI.getFamiliyUpdatePlaceCanUse(it.placeId, it.canUse)
            familyAPI.getFamilyUpdatePlaceInfo(
                it.placeId,
                it.name,
                it.latitude,
                it.longitude
            )
        }

        // 장소의 새로운 상태를 받아온다
        // 어짜피 3개 밖에 없으므로 다 삭제후 넣는다.

        // 기존 정보 삭제
        val oldData = realm.query<FamilyPlace>().find()
        oldData.forEach {
            realm.writeBlocking {
                delete(it)
            }
        }
        // 삽입
        // realm에 저장한다.
        familyAPI.getFamilyPlaceInfo(phoneId)?.forEach {
            val temp = familyAPI.getFamilyPlaceInfoDetail(it.placeId)
            if (temp != null) {
                realm.writeBlocking {
                    FamilyPlace().apply {
                        placeId = temp.placeId
                        name = temp.name
                        latitude = temp.lat
                        longitude = temp.lon
                        canUse = it.canuse
                    }
                }
            }
        }
    }


    // 위험 정보 받고 보내기
    private fun fetchDataMapInstantInfo() {
        // 마지막 시간 보다 변경시간이 작을 경우
        // 모두 보내서 반영한다. -> 수정 필요
        val oldData = realm.query<MapInstantInfo>().find()

        if(oldData!=null){
            oldData.forEach {
                if(it.state =="1"){
                    mapAPI.registMapFacility(true, it.content, it.latitude, it.longitude)
                }else{
                    mapAPI.registMapFacility(false, it.content, it.latitude, it.longitude)
                }
            }
        }

        // 이전 접속 시간을 보내면
        // 그 시간 이후로 받아 온다.
        // => server 변경 후 수정
    }
}
