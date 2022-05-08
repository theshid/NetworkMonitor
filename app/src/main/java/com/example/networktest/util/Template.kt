package com.example.networktest.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.networktest.util.Common
import timber.log.Timber

object Template {


    fun getNetworkType(context: Context):LiveData<String>{
        val networkTypeLiveData = MutableLiveData<String>()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nr = NetworkRequest.Builder()

        cm.registerNetworkCallback(nr.build(), object : ConnectivityManager.NetworkCallback() {

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                Timber.d("hitting callback2")
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        networkTypeLiveData.postValue(Common.WIFI)
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        networkTypeLiveData.postValue(Common.ETHERNET)
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            tm.registerTelephonyCallback(
                                context.mainExecutor,
                                object : TelephonyCallback(),
                                    TelephonyCallback.DataConnectionStateListener {
                                    override fun onDataConnectionStateChanged(
                                        state: Int,
                                        networkType: Int
                                    ) {
                                        Timber.d("hitting callback")
                                        when (networkType) {
                                            TelephonyManager.NETWORK_TYPE_GPRS,
                                            TelephonyManager.NETWORK_TYPE_EDGE,
                                            TelephonyManager.NETWORK_TYPE_CDMA,
                                            TelephonyManager.NETWORK_TYPE_1xRTT,
                                            TelephonyManager.NETWORK_TYPE_IDEN,
                                            TelephonyManager.NETWORK_TYPE_GSM -> {
                                                networkTypeLiveData.postValue(Common.NETWORK2G)
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
                                                networkTypeLiveData.postValue(Common.NETWORK3G)
                                            }
                                            TelephonyManager.NETWORK_TYPE_LTE,
                                            TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> {
                                                networkTypeLiveData.postValue(Common.NETWORK4G)
                                            }
                                            TelephonyManager.NETWORK_TYPE_NR -> {
                                                networkTypeLiveData.postValue(Common.NETWORK5G)
                                            }
                                            else -> networkTypeLiveData.postValue(Common.NETWORKUNKNOWN)
                                        }
                                    }
                                })
                        } else {
                            tm.listen(object : PhoneStateListener() {
                                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                                }

                                override fun onDataConnectionStateChanged(state: Int, networkType: Int) {
                                    super.onDataConnectionStateChanged(state, networkType)
                                    Timber.d("hitting callback old")
                                    when (networkType) {
                                        TelephonyManager.NETWORK_TYPE_GPRS,
                                        TelephonyManager.NETWORK_TYPE_EDGE,
                                        TelephonyManager.NETWORK_TYPE_CDMA,
                                        TelephonyManager.NETWORK_TYPE_1xRTT,
                                        TelephonyManager.NETWORK_TYPE_IDEN,
                                        TelephonyManager.NETWORK_TYPE_GSM -> {
                                            networkTypeLiveData.postValue(Common.NETWORK2G)
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
                                            networkTypeLiveData.postValue(Common.NETWORK3G)
                                        }
                                        TelephonyManager.NETWORK_TYPE_LTE,
                                        TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> {
                                            networkTypeLiveData.postValue(Common.NETWORK4G)
                                        }
                                        TelephonyManager.NETWORK_TYPE_NR -> {
                                            networkTypeLiveData.postValue(Common.NETWORK5G)
                                        }
                                        else -> networkTypeLiveData.postValue(Common.NETWORKUNKNOWN)
                                    }
                                }
                            }, PhoneStateListener.LISTEN_CALL_STATE)
                        }
                    }
                    else -> {
                        networkTypeLiveData.postValue(Common.NETWORKUNKNOWN)

                    }
                }
            }
        })
        return networkTypeLiveData
    }

    fun getNetworkStatus(context: Context): LiveData<Boolean> {
        val isAvailableLiveData = MutableLiveData<Boolean>()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nr = NetworkRequest.Builder()

        cm.registerNetworkCallback(nr.build(), object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isAvailableLiveData.postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isAvailableLiveData.postValue(false)

            }

        })
        return isAvailableLiveData
    }
}