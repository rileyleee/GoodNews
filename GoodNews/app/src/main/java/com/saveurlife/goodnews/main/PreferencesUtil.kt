package com.saveurlife.goodnews.main

import android.content.Context
import android.content.SharedPreferences

class PreferencesUtil(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("gn_sharedpref", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return preferences.getString(key, defValue).toString()
    }

    fun setString(key: String, defValue: String) {
        preferences.edit().putString(key, defValue).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun setInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun setLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getFloat(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun setFloat(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    // Shared Preferences는 Double 타입을 지원하지 않기 때문에 String으로 저장하고 변환 필요
    fun getDouble(key: String, defValue: Double): Double {
        val doubleString = preferences.getString(key, null)
        return doubleString?.toDoubleOrNull() ?: defValue
    }

    fun setDouble(key: String, value: Double) {
        preferences.edit().putString(key, value.toString()).apply()
    }

    //key 삭제
    fun removeKey(key: String){
        preferences.edit().remove(key).apply()
    }

    companion object {
        @Volatile private var instance: PreferencesUtil? = null

        fun getInstance(context: Context): PreferencesUtil =
            instance ?: synchronized(this) {
                instance ?: PreferencesUtil(context.applicationContext).also { instance = it }
            }
    }

}