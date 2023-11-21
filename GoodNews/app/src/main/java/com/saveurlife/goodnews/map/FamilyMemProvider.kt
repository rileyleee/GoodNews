package com.saveurlife.goodnews.map

import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FamilyMemInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FamilyMemProvider {

    // realm에서 가족 리스트 뽑아와서 지도에 띄우기
    private var familyMemInfo: MutableList<FamilyMemInfo> = mutableListOf() // 초기화

    fun getFamilyMemInfo(): MutableList<FamilyMemInfo> {
        Log.d("FamilyMemProvider","가족 정보 찾으러 왔어요")

        CoroutineScope(Dispatchers.IO).launch {

            var realm = Realm.open(GoodNewsApplication.realmConfiguration)

            var familyList = realm.query<FamilyMemInfo>().find()

            // 이전 데이터를 지우기
            familyMemInfo.clear()

            if (familyList.isNotEmpty()) {
                familyList.forEach { fam ->
                    familyMemInfo.add(fam)
                }
            }
            realm.close()
        }

        return familyMemInfo
    }

    // BLE로 연결된 내역에서 가족인 경우에는 필터링해서 realm에 저장하는 방식으로
    // 마커가 두 번 찍히지 않게 처리
}

