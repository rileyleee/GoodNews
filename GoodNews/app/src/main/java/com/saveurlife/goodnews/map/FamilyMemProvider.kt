package com.saveurlife.goodnews.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FamilyMemInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyMemProvider {

    // realm에서 가족 리스트 뽑아와서 지도에 띄우기
    private var familyMemInfo: MutableList<FamilyMemInfo> = mutableListOf() // 초기화

    private val allFamilyMemIds = MutableLiveData<List<String>>()

    fun getAllFamilyMemIds(): LiveData<List<String>> {
        return allFamilyMemIds
    }


    fun updateAllFamilyMemIds() {
        CoroutineScope(Dispatchers.IO).launch {
            val realm = Realm.open(GoodNewsApplication.realmConfiguration)
            try {
                val familyList = realm.query<FamilyMemInfo>().find()
                val familyMemIds = familyList.map { it.id }

                withContext(Dispatchers.Main) {
                    allFamilyMemIds.value = familyMemIds
                }
            } finally {
                realm.close()
            }
        }
    }


    suspend fun getFamilyMemInfo(): MutableList<FamilyMemInfo> {
        Log.d("FamilyMemProvider", "가족 정보 찾으러 왔어요")

        return withContext(Dispatchers.IO) {
            var realm = Realm.open(GoodNewsApplication.realmConfiguration)

            try {
                var familyList = realm.query<FamilyMemInfo>().find()

                // 이전 데이터를 지우기
                familyMemInfo.clear()

                if (familyList.isNotEmpty()) { // 깊은 복사 수행
                    familyList.forEach { fam ->

                        Log.v("realm에서 꺼낸 가족 정보", "${fam.name}")
                        val copiedFam = FamilyMemInfo().apply {
                            this.id = fam.id
                            this.name = fam.name
                            this.latitude = fam.latitude
                            this.longitude = fam.longitude
                            this.lastConnection = fam.lastConnection
                            this.state = fam.state
                        }
                        familyMemInfo.add(copiedFam)
                    }
                }

                Log.v("복사된 가족 정보", "${familyMemInfo.size}")
                familyMemInfo
            } finally {
                realm.close()
            }
        }
    }
}

