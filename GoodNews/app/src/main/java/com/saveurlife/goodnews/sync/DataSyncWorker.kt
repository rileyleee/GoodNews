package com.saveurlife.goodnews.sync

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class DataSyncWorker (private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){
    // 백그라운드에서 인터넷 연결을 기다렸다가 동기화를 해야할 경우 사용!

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

        val syncService = SyncService(context)

        try {
            syncService.fetchAllData()
        } catch (e : Exception) {
            Log.e(TAG_ERR, "데이터를 불러오지 못했습니다. : " +e.toString())
            return Result.failure()
        } finally {
            Log.e(TAG_ERR, "최신 정보로 업데이트 했습니다.")
            return Result.success()
        }
    }
}
