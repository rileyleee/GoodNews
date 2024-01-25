package com.saveurlife.goodnews.ble

import android.util.Log
import android.widget.Toast
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.sync.SyncService
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

import java.util.*

class DangerInfoRealmRepository {

    fun saveDangerInfoToRealm(dangerInfo: String) {
        val infoParts = dangerInfo.split("@")

        if (infoParts.size == 5) {
            val date = convertIso8601ToDate(infoParts[1])


            val state = infoParts[0]
            val time = RealmInstant.from(date.time / 1000, (date.time % 1000).toInt())
            val latitude = infoParts[2].toDouble()
            val longitude = infoParts[3].toDouble()
            val content = infoParts[4]

            // 현재 정보 realm에 저장
//            CoroutineScope(Dispatchers.IO).launch {
//                val realm = Realm.open(GoodNewsApplication.realmConfiguration)
//                try {
//                    realm.write {
//                        copyToRealm(MapInstantInfo(state, content, time, latitude, longitude))
//                    }
//                } catch (e: Exception) {
//                    Log.e("EmergencyInfoDialogFragment", "긴급 정보를 Realm에 저장하는 과정에서 오류", e)
//                } finally {
//                    realm.close()
//                }
//            }
        } else {
            Log.e("EmergencyInfoDialogFragment", "dangerInfo의 형식이 올바르지 않습니다.")
        }
    }

    fun convertIso8601ToDate(iso8601Date: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormat.parse(iso8601Date) ?: Date(0)
    }

}