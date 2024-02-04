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
import kotlin.properties.Delegates

class DataSyncWorker (private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){

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

    private val TAG_ERR = "BACKGROUND SYNC ERROR"

    override fun doWork(): Result {

        val dataSync = DataSync(context)

        try {
            // 1. 회원 가입 정보 -> member table
            dataSync.fetchDataMember()
            // 2. 가족 구성원 정보 -> familymem_info
            dataSync.fetchDataFamilyMemInfo()
            // 3. 가족 모임 장소 -> family_place
            dataSync.fetchDataFamilyPlace()
            // 4. 지도 정보 - 버튼 정보 받기
            dataSync.fetchDataMapInstantInfo()

        } catch (e : Exception) {
            Log.e(TAG_ERR, "데이터를 불러오지 못했습니다. : " +e.toString())
            return Result.failure()
        } finally {
            Log.e(TAG_ERR, "최신 정보로 업데이트 했습니다.")
            return Result.success()
        }
    }
}
