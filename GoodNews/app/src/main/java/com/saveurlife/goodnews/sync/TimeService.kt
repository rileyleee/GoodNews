package com.saveurlife.goodnews.sync

import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.main.PreferencesUtil
import io.realm.kotlin.types.RealmInstant
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class TimeService {
    private val preferences: PreferencesUtil = GoodNewsApplication.preferences

    // 동기화 시간 반환
    // Long -> yyyy-MM-dd HH:mm:ss
    fun getLastConnectionTime(): String {
        val millisecond = preferences.getLong("SyncTime", 0L)

        return convertMillisToDateTime(millisecond, "yyyy-MM-dd HH:mm:ss")
    }

    fun convertLongToStr(time : Long):String{
        return convertMillisToDateTime(time, "yyyy-MM-dd HH:mm:ss")
    }

    // 시간 형태 변환
    // "YYYY-MM-DDTHH:mm:ss" -> Long 형태
    // ex) "2023-11-13T03:12:02"
    // 이걸 사용하는 것은 서버 -> 앱 밖에 없으므로 시간대가 서울 시간대에서 변경임.
    fun convertDateStrToLong(oldTime: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val localDateTime = LocalDateTime.parse(oldTime, formatter)

        return localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
    }

    // "YYYY-MM=DDTHH:mm:ss -> realmInstant
    fun convertDateToRealmInstance(time: String): RealmInstant{
        var longTime = convertDateStrToLong(time)
        return convertLongToRealmInstant(longTime)
    }

    private fun convertMillisToDateTime(millis: Long, dateFormat: String, locale: Locale = Locale.getDefault()): String {
        val sdf = SimpleDateFormat(dateFormat, locale)
        val date = Date(millis)
        return sdf.format(date)
    }

    // 시간 형태 변환
    // Long -> "YYYY-MM-DD HH:mm:ss"
    // ex) "2023-11-12 03:12:02"

    fun convertDateLongToString(oldTime: Long): String {
        val dateFormat = "yyyy-MM-dd HH:mm:ss"

        return convertTimestampToString(oldTime, dateFormat)
    }
    private fun convertTimestampToString(timestamp: Long, dateFormat: String): String {
        try {
            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertDateStringToNumStr(oldText: String): String {
        // 날짜 형식 지정
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale("ko"))

        // 날짜로 변환
        val date = dateFormat.parse(oldText)

        // 원하는 형식으로 포맷팅
        return SimpleDateFormat("yyyyMMdd", Locale("ko")).format(date)
    }

    fun realmInstantToString(realmInstant: RealmInstant): String {
        // RealmInstant을 밀리초로 변환
        val milliseconds = realmInstant.epochSeconds * 1000 + realmInstant.nanosecondsOfSecond

        // 밀리초를 LocalDateTime 객체로 변환
        val alertTime = LocalDateTime.ofInstant(Date(milliseconds).toInstant(), ZoneId.systemDefault())

        // 9시간 전으로 변경
        val nineHoursBefore = alertTime.minusHours(9)

        // LocalDateTime을 다시 Date 객체로 변환
        val date = Date.from(nineHoursBefore.atZone(ZoneId.systemDefault()).toInstant())

//        // 밀리초를 Date 객체로 변환
//        val date = Date(milliseconds)

        // SimpleDateFormat을 사용하여 문자열로 포맷팅
        val dateFormat = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun realmInstantToLong(realmInstant: RealmInstant): Long {
        // realmInstant를 밀리초로 변환
        return realmInstant.epochSeconds * 1000 + realmInstant.nanosecondsOfSecond
    }

    // Long으로 된 ms 시간 -> RealmInstance 변경
    fun convertLongToRealmInstant(time:Long):RealmInstant{
        return RealmInstant.from(time / 1000, (time % 1000).toInt())
    }
}