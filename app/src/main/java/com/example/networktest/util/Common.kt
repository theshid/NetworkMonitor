package com.example.networktest.util

import android.content.IntentFilter

object Common {
    const val WIFI = "WIFI"
    const val ETHERNET = "ETHERNET"
    const val NETWORK2G = "2G"
    const val NETWORK3G = "3G"
    const val NETWORK4G = "4G"
    const val NETWORK5G = "5G"
    const val NETWORKUNKNOWN = "NETWORK UNKNOWN"
    const val SHAREDPREFKEY = "NETWORK"
    const val INTENT_ACTION = "com.networktest.MONITOR_ACTION"

    val intentFilter = IntentFilter(INTENT_ACTION)

}