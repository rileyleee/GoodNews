package com.saveurlife.goodnews.alert

import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.models.Alert
import io.realm.kotlin.types.RealmInstant
import java.util.concurrent.TimeUnit

class AlertRepository(private val alarmDatabaseManger: AlertDatabaseManager){

    private val AlertLiveData = mutableMapOf<String, MutableLiveData<List<Alert>>>()

    //구조요청 알림 저장
    fun addSaveAlert(senderId: String, name: String, content: String, latitude: Double, longitude: Double, time: String, type: String) {
        val currentTimeMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(9)
        val realmInstant = RealmInstant.from(currentTimeMillis/1000, (currentTimeMillis%1000).toInt())

        val alert = Alert(id = "R$senderId", senderId=senderId, name = name, content = content, latitude = latitude, longitude = longitude, time = realmInstant, type= type)
        alarmDatabaseManger.createAlarm(alert){
            updateAlertLiveData(alert)
        }
    }

    //가족 연결 알림 저장
    fun addFamilyAlert(senderId: String, name: String, content: String, latitude: Double, longitude: Double, time: String, type: String) {
        val currentTimeMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(9)
        val realmInstant = RealmInstant.from(currentTimeMillis/1000, (currentTimeMillis%1000).toInt())

        println("가족 연결 알림 AlertRepository")

        val alert = Alert(id = "F$realmInstant$senderId", senderId=senderId, name = name, content = content, latitude = latitude, longitude = longitude, time = realmInstant, type= type)
        alarmDatabaseManger.createAlarm(alert){
            updateAlertLiveData(alert)
        }
    }

    //가족 장소 알림 저장
    fun editFamilyPlaceAlert(senderId: String, name: String, type: String) {
        val currentTimeMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(9)
        val realmInstant = RealmInstant.from(currentTimeMillis/1000, (currentTimeMillis%1000).toInt())

        val alert = Alert(id = "$realmInstant$senderId", senderId=senderId, name = name, content = "멀쩡함", latitude = 0.0, longitude = 0.0, time = realmInstant, type= type)
        alarmDatabaseManger.createAlarm(alert){
            updateAlertLiveData(alert)
        }
    }



    private fun updateAlertLiveData(alert: Alert) {
        //여기에 추가하는거
//        val currentList = AlertLiveData[chatRoomId]?.value ?: emptyList()
//        AlertLiveData[chatRoomId]?.postValue(currentList + newMessage)

    }
}