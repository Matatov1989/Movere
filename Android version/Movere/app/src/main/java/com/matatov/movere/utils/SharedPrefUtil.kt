package com.matatov.movere.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefUtil {

    const val FILE_NAME = "MoverePref"
    const val IS_INTRO_OPENED = "isIntroOpened"

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    fun putIntPref(key: String?, value: Int, context: Context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        editor!!.putInt(key, value)
        editor!!.commit()
    }

    fun getIntPref(key: String?, defaultValue: Int, context: Context): Int {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences!!.getInt(key, defaultValue)
    }


    fun putLongPref(key: String?, value: Long, context: Context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        editor!!.putLong(key, value)
        editor!!.commit()
    }

    fun getLongPref(key: String?, defaultValue: Long, context: Context): Long {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences!!.getLong(key, defaultValue)
    }


    fun putBooleanPref(key: String?, value: Boolean, context: Context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun getBooleanPref(key: String?, defaultValue: Boolean, context: Context): Boolean {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean(key, defaultValue)
    }


    fun putStrPref(key: String?, value: String, context: Context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun getStrPref(key: String?, defaultValue: String, context: Context): String? {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences!!.getString(key, defaultValue)
    }


    fun putFloatPref(key: String?, value: Float, context: Context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        editor!!.putFloat(key, value)
        editor!!.commit()
    }

    fun getFloatPref(key: String?, defaultValue: Float, context: Context): Float {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences!!.getFloat(key, defaultValue)
    }

    fun clearPref(context: Context){
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences!!.edit().clear().apply()
    }
}
