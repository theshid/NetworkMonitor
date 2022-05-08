package com.example.networktest.util

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    private val mySharePref: SharedPreferences =
        context.getSharedPreferences("filename", Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = mySharePref.edit()

    fun saveNetworkType(networkType: String) {
        editor.putString(Common.SHAREDPREFKEY, networkType)
        editor.apply()
    }

    fun getNetworkTypeFromSharedPref(): String {
        return mySharePref.getString(Common.SHAREDPREFKEY,"").toString()
    }
}