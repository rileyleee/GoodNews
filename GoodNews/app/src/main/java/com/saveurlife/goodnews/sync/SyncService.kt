package com.saveurlife.goodnews.sync

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class SyncService(private val context: Context) {

    private val dataSync : DataSync = DataSync(context)

    val familyMemInfoUpdatedInit = MutableLiveData<Boolean>()
    val familyPlaceUpdatedInit = MutableLiveData<Boolean>()

    val familyMemInfoUpdatedOne = MutableLiveData<Boolean>()
    val familyPlaceUpdatedOne = MutableLiveData<Boolean>()

    val familyMemInfoUpdated = MutableLiveData<Boolean>()
    val familyPlaceUpdated = MutableLiveData<Boolean>()

    fun fetchAllData(){
        dataSync.init()
        dataSync.fetchDataMember()
        dataSync.fetchDataFamilyMemInfo(familyMemInfoUpdatedInit)
        dataSync.fetchDataFamilyPlace(familyPlaceUpdatedInit)
        dataSync.fetchDataMapInstantInfo()
    }

    // 전체 변경일 경우
    fun fetchFamilyData(){
        dataSync.init()
        dataSync.fetchDataFamilyMemInfo(familyMemInfoUpdated)
        dataSync.fetchDataFamilyPlace(familyPlaceUpdated)
    }

    // 일부만 변경일 경우
    fun fetchFamilyNewData(){
        dataSync.init()
        dataSync.fetchDataFamilyMemInfo(familyMemInfoUpdatedOne)
        dataSync.fetchDataFamilyPlace(familyPlaceUpdatedOne)
    }

    fun fetchFacilityData(){
        dataSync.init()
        dataSync.fetchDataMapInstantInfo()
    }

    fun backGroundSync(){
        dataSync.init()
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