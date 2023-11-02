package com.saveurlife.goodnews.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val isOnFlash: MutableLiveData<Boolean> = MutableLiveData(false)
}