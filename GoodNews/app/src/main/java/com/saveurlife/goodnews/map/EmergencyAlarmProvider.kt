package com.saveurlife.goodnews.map

import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

class EmergencyAlarmProvider {

    private lateinit var realm: Realm
    private lateinit var userInfo: Member
    private lateinit var targetInfo: RealmResults<MapInstantInfo>
    private var closeInfo = mutableListOf<MapInstantInfo>()

    fun getAlarmInfo(): MapInstantInfo? {

        CoroutineScope(Dispatchers.IO).launch {
            realm = Realm.open(GoodNewsApplication.realmConfiguration)

            // 나의 위치 조회
            userInfo = realm.query<Member>().find().first()

            // 모든 위험 정보 조회
            targetInfo = realm.query<MapInstantInfo>("state=$0", "1").find()

            // 50m 이내에 있는 정보라면! 다시 가까운 정보 리스트에 담고
            targetInfo.forEach { info ->
                if (getRoughDistance(
                        info.latitude,
                        info.longitude,
                        userInfo.latitude,
                        userInfo.longitude
                    ) <= 50
                ) {
                    closeInfo.add(info)
                }
            }
            realm.close()
        }
        
        // 가장 최근 정보 담기
        val mostRecentInfo:MapInstantInfo ? = closeInfo.maxByOrNull { it.time.epochSeconds }
        mostRecentInfo?.let {
            Log.v("mostRecentInfo", "가장 최근 정보: ${it.content}")
        }
        return mostRecentInfo
    }


    fun getRoughDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

        // 위도와 경도 차이 계산
        val latDifference = abs(lat1 - lat2)
        val lonDifference = abs(lon1 - lon2)

        // 위도와 경도의 차이를 미터로 환산
        val latDistance = latDifference * 111000
        val lonDistance = lonDifference * cos(Math.toRadians(lat1)) * 111000

        // 사각형 대각선 거리 계산
        return sqrt(latDistance * latDistance + lonDistance * lonDistance)
    }


}