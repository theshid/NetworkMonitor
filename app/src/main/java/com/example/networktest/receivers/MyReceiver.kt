package com.example.networktest.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.networktest.showToast
import com.example.networktest.util.Common
import com.example.networktest.util.SharedPref

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref = SharedPref(context)
        val savedNetworkType = sharedPref.getNetworkTypeFromSharedPref()
        if (Common.INTENT_ACTION == intent.action) {
            val connectionType = intent.getStringExtra("extra")
            if (connectionType != savedNetworkType) {
                if (connectionType != null) {
                    sharedPref.saveNetworkType(connectionType)
                    context.showToast(connectionType)
                }
            }
        }
    }
}