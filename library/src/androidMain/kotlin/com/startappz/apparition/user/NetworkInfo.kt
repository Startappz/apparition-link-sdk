package com.startappz.apparition.user

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class NetworkInfo(
    private val context: Context,
) {

    fun getConnectionType(): String {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return UNKNOWN
        }

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> WIFI
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> MOBILE
            else -> UNKNOWN
        }
    }


    fun getCarrier(): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperatorName ?: "undefined"
    }

    companion object {
        const val WIFI = "wifi"
        const val MOBILE = "mobile"
        const val UNKNOWN = "unknown"
    }
}