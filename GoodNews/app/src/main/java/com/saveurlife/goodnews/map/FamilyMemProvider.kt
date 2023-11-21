package com.saveurlife.goodnews.map

import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.FamilyMemInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class FamilyMemProvider {

    // realm에서 가족 리스트 뽑아와서 지도에 띄우기

    private var realm = Realm.open(GoodNewsApplication.realmConfiguration)
    private lateinit var familyMemInfo: MutableList<FamilyMemInfo>

    fun getFamilyMemInfo():MutableList<FamilyMemInfo> {

        var familyList = realm.query<FamilyMemInfo>().find()

        familyList.forEach { fam ->
            familyMemInfo.add(fam)
        }

        return familyMemInfo
    }

    // BLE로 연결된 내역에서 가족인 경우에는 필터링해서 realm에 저장하는 방식으로
    // 마커가 두 번 찍히지 않게 처리
}

