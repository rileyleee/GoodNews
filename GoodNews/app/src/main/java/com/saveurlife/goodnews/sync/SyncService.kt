package com.saveurlife.goodnews.sync

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class SyncService(private val context: Context) {

    private val dataSync : DataSync = DataSync(context)

    val familyMemInfoUpdated = MutableLiveData<Boolean>()
    val familyPlaceUpdated = MutableLiveData<Boolean>()

    fun fetchAllData(){
        dataSync.fetchDataMember()
        dataSync.fetchDataFamilyMemInfo()
        dataSync.fetchDataFamilyPlace()
        dataSync.fetchDataMapInstantInfo()

        familyMemInfoUpdated.postValue(true)
        familyPlaceUpdated.postValue(true)
    }

    fun fetchFamilyData(){
        dataSync.fetchDataFamilyPlace()
        dataSync.fetchDataFamilyMemInfo()

        familyMemInfoUpdated.postValue(true)
        familyPlaceUpdated.postValue(true)
    }

    fun fetchFacilityData(){
        dataSync.fetchDataMapInstantInfo()
    }

    fun backGroundSync(){
        // WorkManager
        val workManager = WorkManager.getInstance(context)

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
    }
}