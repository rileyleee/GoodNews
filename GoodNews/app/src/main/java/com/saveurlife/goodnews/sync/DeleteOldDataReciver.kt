package com.saveurlife.goodnews.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.models.MapInstantInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DeleteOldDataReciver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val timeService = TimeService()

        fun deleteOldData(){
            // 시간 기준 필터링 - 일주일 기준으로
            val time = System.currentTimeMillis() - System.currentTimeMillis()

            GlobalScope.launch {
                realm.writeBlocking {
                    val oldData = realm.query<MapInstantInfo>().find()
                    val result: List<MapInstantInfo> = oldData.filter { it.time <= timeService.convertLongToRealmInstant(time) }

                    result.forEach{
                        delete(it)
                    }
                }
            }
        }
    }

    // 24시간마다 오래된 데이터 삭제 작업
}