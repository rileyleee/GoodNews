package com.saveurlife.goodnews.sync

import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.main.PreferencesUtil
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SyncService {
    private val preferences: PreferencesUtil = GoodNewsApplication.preferences

    // 동기화 시간 반환
    fun getLastConnectionTime(): String {
        val millisecond = preferences.getLong("SyncTime", 0L)

        return convertMillisToDateTime(millisecond, "yyyy-MM-dd HH:mm:ss")
    }

    // 시간 형태 변환
    // "YYYY-MM-DDTHH:mm:ss" -> Long 형태
    // ex) "2023-11-13T03:12:02"
    fun convertDateStrToLong(oldTime: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val localDateTime = LocalDateTime.parse(oldTime, formatter)

        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
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

}