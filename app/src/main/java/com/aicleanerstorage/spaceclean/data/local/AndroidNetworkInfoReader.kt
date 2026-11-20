package com.aicleanerstorage.spaceclean.data.local

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.domain.model.NetworkInfoModel

internal class AndroidNetworkInfoReader(
    context: Context,
) {
    private val appContext = context.applicationContext

    fun read(): NetworkInfoModel {
        val connectivity = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork)
        val type =
            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> appContext.getString(R.string.wifi)
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> appContext.getString(R.string.network_type_cellular)
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> appContext.getString(R.string.network_type_ethernet)
                else -> "--"
            }

        val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val wifiInfo = wifiManager?.connectionInfo
        val ssid =
            wifiInfo
                ?.ssid
                ?.removeSurrounding("\"")
                ?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
                ?: appContext.getString(R.string.unknown_ssid)
        val ip =
            wifiInfo
                ?.ipAddress
                ?.takeIf { it != 0 }
                ?.let { Formatter.formatIpAddress(it) }
                ?: "--"

        return NetworkInfoModel(
            type = type,
            ssid = ssid,
            ip = ip,
        )
    }
}

