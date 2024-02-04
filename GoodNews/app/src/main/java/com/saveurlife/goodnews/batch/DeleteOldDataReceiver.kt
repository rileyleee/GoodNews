package com.saveurlife.goodnews.batch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.sync.TimeService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import java.util.concurrent.TimeUnit

class DeleteOldDataReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Deleting old data", "오래된 데이터를 삭제중입니다.")

        val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val timeService = TimeService()

        // 시간 기준 필터링 - 일주일 기준으로
        val time = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)

        try {
            realm.writeBlocking {
                val oldData = realm.query<MapInstantInfo>().find()
                val result: List<MapInstantInfo> = oldData.filter { it.time <= timeService.convertLongToRealmInstant(time) }

                result.forEach{
                    delete(it)
                }
            }
        } finally {
            realm.close()
        }
    }
}