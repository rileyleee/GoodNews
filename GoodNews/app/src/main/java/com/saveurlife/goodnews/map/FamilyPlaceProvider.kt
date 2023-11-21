package com.saveurlife.goodnews.map

import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FamilyPlaceProvider {


    // realm에서 가족 장소 뽑아와서 지도에 띄우기
    private var familyPlaceInfo: MutableList<FamilyPlace> = mutableListOf() // 초기화

    fun getFamilyPlace(): MutableList<FamilyPlace> {
        CoroutineScope(Dispatchers.IO).launch {
            var realm = Realm.open(GoodNewsApplication.realmConfiguration)

            var familyPlaceList = realm.query<FamilyPlace>().find()

            // 이전 데이터 삭제
            familyPlaceInfo.clear()

            if (familyPlaceList.isNotEmpty()) {
                familyPlaceList.forEach { famPlace ->
                    familyPlaceInfo.add(famPlace)
                }
            }
            realm.close()
        }

        return familyPlaceInfo
    }


}