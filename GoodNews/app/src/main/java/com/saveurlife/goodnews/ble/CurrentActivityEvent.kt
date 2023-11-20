package com.saveurlife.goodnews.ble

import org.greenrobot.eventbus.EventBus

class CurrentActivityEvent(val activityName: String)
object EventBusManager {
    fun post(event: Any) {
        EventBus.getDefault().post(event)
    }
}