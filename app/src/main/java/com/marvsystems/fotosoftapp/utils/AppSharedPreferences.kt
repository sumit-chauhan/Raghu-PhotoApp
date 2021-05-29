package com.marvsystems.fotosoftapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * @author Anshul Gour on 19,June,2019
    Description : -
 */
class AppSharedPreferences private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {

        private var appSharedPref: AppSharedPreferences? = null

        fun getInstance(context: Context): AppSharedPreferences {
            if (appSharedPref == null) {
                appSharedPref = AppSharedPreferences(context)
            }
            return appSharedPref as AppSharedPreferences
        }
    }

    fun setString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, "NA")
    }


    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }


    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }

}