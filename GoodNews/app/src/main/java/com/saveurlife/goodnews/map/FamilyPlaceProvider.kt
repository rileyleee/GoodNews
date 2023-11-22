package com.saveurlife.goodnews.map

import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyPlaceProvider {

    // realm에서 가족 장소 뽑아와서 지도에 띄우기
    private var familyPlaceInfo: MutableList<FamilyPlace> = mutableListOf() // 초기화

    suspend fun getFamilyPlace(): MutableList<FamilyPlace> {
        Log.d("FamilyMemProvider", "가족 약속 장소 찾으러 왔어요")
        return withContext(Dispatchers.IO) {
            var realm = Realm.open(GoodNewsApplication.realmConfiguration)
            try {
                var familyPlaceList = realm.query<FamilyPlace>().find()

                // 이전 데이터 삭제
                familyPlaceInfo.clear()

                if (familyPlaceList.isNotEmpty()) {
                    familyPlaceList.forEach { famPlace ->
                        Log.v("realm에서 꺼낸 가족 약속장소 정보", "${famPlace.name}")
                        val copiedFamPlace = FamilyPlace().apply {
                            this.placeId = famPlace.placeId
                            this.name = famPlace.name
                            this.latitude = famPlace.latitude
                            this.longitude = famPlace.longitude
                            this.canUse = famPlace.canUse
                            this.address = famPlace.address
                        }
                        familyPlaceInfo.add(copiedFamPlace)
                    }
                }
                Log.v("복사된 가족 약속장소 정보", "${familyPlaceInfo.size}")
                familyPlaceInfo
            } finally {
                realm.close()
            }
        }
    }
}