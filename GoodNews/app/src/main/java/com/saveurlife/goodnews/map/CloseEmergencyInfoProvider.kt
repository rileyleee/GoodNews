package com.saveurlife.goodnews.map

import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.MapInstantInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

class CloseEmergencyInfoProvider {

    private lateinit var realm: Realm
    private lateinit var targetInfo: RealmResults<MapInstantInfo>
    private var closeInfo = mutableListOf<MapInstantInfo>()


    fun getCloseEmergencyInfo(centerLat: Double, centerLon: Double, dist: Int): MutableList<MapInstantInfo> {

        // 클릭 시 마다 새로운 리스트 생성 위해 초기화 작업
        closeInfo.clear()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                realm = Realm.open(GoodNewsApplication.realmConfiguration)

                // 모든 위험 정보 조회
                targetInfo = realm.query<MapInstantInfo>("state = $0", "1").find()
                Log.v("T: CloseEmergencyInfoProvider", targetInfo.size.toString())

                // 100m 이내에 있는 정보라면! 가까운 정보 리스트에 담고
                targetInfo.forEach { info ->
                    if (getRoughDistance(
                            info.latitude,
                            info.longitude,
                            centerLat,
                            centerLon
                        ) <= dist
                    ) {
                        closeInfo.add(
                            copyMapInstantInfo(info)
                        )
                    }
                }

            } catch (e: RealmException) {
                Log.e("CloseEmergencyInfoProvider", "Realm 작업 중 에러 발생", e)
            } finally {
                realm.close()
            }
            Log.v("C: CloseEmergencyInfoProvider", closeInfo.size.toString())

        }
        return closeInfo

    }


    // realm 객체에서 직접 작업 불가 -> 복사
    private fun copyMapInstantInfo(info: MapInstantInfo): MapInstantInfo {

        return MapInstantInfo().apply {
            this.state = info.state
            this.latitude = info.latitude
            this.longitude = info.longitude
            this.content = info.content
            this.time = info.time
        }
    }


    private fun getRoughDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

        // 위도와 경도 차이 계산
        val latDifference = abs(lat1 - lat2)
        val lonDifference = abs(lon1 - lon2)

        // 위도와 경도의 차이를 미터로 환산
        val latDistance = latDifference * 111000
        val lonDistance = lonDifference * cos(Math.toRadians(lat1)) * 111000

        // 사각형 대각선 거리 계산
        Log.v("두 좌표 간의 거리",sqrt(latDistance * latDistance + lonDistance * lonDistance).toString())
        return sqrt(latDistance * latDistance + lonDistance * lonDistance)
    }
}