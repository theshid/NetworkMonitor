package com.example.networktest.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.networktest.receivers.MyReceiver
import com.example.networktest.PermissionUtils
import com.example.networktest.R
import com.example.networktest.showSnackbar
import com.example.networktest.showToast
import com.example.networktest.util.Common
import com.example.networktest.util.Template
import kotlinx.coroutines.*
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {
    private val PHONE_STATE_PERMISSION_REQUEST_CODE = 999
    private var networkTypeString = ""
    private val broadcastReceiver = MyReceiver()
    lateinit var myJob: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = findViewById<View>(android.R.id.content)
        registerReceiver(broadcastReceiver, Common.intentFilter)
        checkIfPermissionIsActive()
        onNetworkChange { isConnected ->
            if (isConnected) {
                showSnackbar(rootView,"Connected")
            } else {
                showSnackbar(rootView,"Not Connected",true)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        myJob.cancel()
        unregisterReceiver(broadcastReceiver)
    }

    private fun onNetworkChange(block: (Boolean) -> Unit) {
        Template.getNetworkStatus(this)
            .observe(this, Observer { isConnected ->
                block(isConnected)
            })
    }

    private fun onNetworkTypeChange2(block: (String) -> Unit) {
        Template.getNetworkType(this).observe(this, Observer { connectionType ->
            block(connectionType)
        })
    }

    @OptIn(InternalCoroutinesApi::class)
    protected fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (NonCancellable.isActive) {
                // add your task here
                monitor()
                Timber.d("test")
                delay(timeInterval)
            }
        }
    }

    private fun sendBroadcast(networkType: String) {
        val intent = Intent(Common.INTENT_ACTION)
        intent.putExtra("extra", networkType)
        sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    private fun monitor() {
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when (tm.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN,
                TelephonyManager.NETWORK_TYPE_GSM -> {
                    Timber.d(Common.NETWORK2G)
                    sendBroadcast(Common.NETWORK2G)
                    return
                }
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> {
                    Timber.d(Common.NETWORK3G)
                    sendBroadcast(Common.NETWORK3G)
                    return
                }
                TelephonyManager.NETWORK_TYPE_LTE,
                TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> {
                    Timber.d(Common.NETWORK4G)
                    sendBroadcast(Common.NETWORK4G)
                    return
                }
                TelephonyManager.NETWORK_TYPE_NR -> {
                    Timber.d(Common.NETWORK5G)
                    sendBroadcast(Common.NETWORK5G)
                    return
                }
                else -> {
                    Timber.d(Common.NETWORKUNKNOWN)
                    sendBroadcast(Common.NETWORKUNKNOWN)
                    return
                }
            }
        } else {
            when (tm.networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN,
                TelephonyManager.NETWORK_TYPE_GSM -> {
                    Timber.d(Common.NETWORK2G)
                    sendBroadcast(Common.NETWORK2G)
                    return
                }
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> {
                    Timber.d(Common.NETWORK3G)
                    sendBroadcast(Common.NETWORK3G)
                    return
                }
                TelephonyManager.NETWORK_TYPE_LTE,
                TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> {
                    Timber.d(Common.NETWORK4G)
                    sendBroadcast(Common.NETWORK4G)
                    return
                }
                TelephonyManager.NETWORK_TYPE_NR -> {
                    Timber.d(Common.NETWORK5G)
                    sendBroadcast(Common.NETWORK5G)
                    return
                }
                else -> {
                    Timber.d(Common.NETWORKUNKNOWN)
                    sendBroadcast(Common.NETWORKUNKNOWN)
                    return
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PHONE_STATE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myJob = startRepeatingJob(3000)
                    onNetworkTypeChange2 { connectionType ->
                        showToast(connectionType)
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.permisson_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkIfPermissionIsActive() {
        when {
            PermissionUtils.isAccessPhoneStateGranted(this) -> {
                myJob = startRepeatingJob(3000)
                onNetworkTypeChange2 { connectionType ->
                    if (networkTypeString != connectionType) {
                        networkTypeString = connectionType
                        showToast(connectionType)
                    }
                }
            }
            else -> {
                PermissionUtils.requestPhoneStatePermission(
                    this,
                    PHONE_STATE_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

}