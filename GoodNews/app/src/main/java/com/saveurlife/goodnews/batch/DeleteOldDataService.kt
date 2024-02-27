package com.saveurlife.goodnews.batch

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.util.Log

class DeleteOldDataService(private val context:Context) {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    companion object{
        private var instance: DeleteOldDataService? = null
        fun getInstance(context:Context): DeleteOldDataService{
            if(instance == null){
                instance = DeleteOldDataService(context)
            }
            return instance as DeleteOldDataService
        }
    }
    fun setDeleteManager(){
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, DeleteOldDataReceiver::class.java).let {
            intent -> PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )

        // 테스트 코드 - 1분마다 실행
//        alarmMgr?.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            1000*60*1,
//            alarmIntent
//        )
        Log.d("Setting Old Data", "Setting Completed")
    }

    fun cancelDelete(){
        alarmMgr?.cancel(alarmIntent)
    }
}