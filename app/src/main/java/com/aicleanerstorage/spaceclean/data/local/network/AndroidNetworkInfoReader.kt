package com.aicleanerstorage.spaceclean.data.local.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkTransport

internal class AndroidNetworkInfoReader(
    context: Context,
) {
    private val appContext = context.applicationContext

    fun read(): NetworkInfoModel {
        val connectivity = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork)
        val transport =
            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkTransport.Wifi
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkTransport.Cellular
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkTransport.Ethernet
                else -> NetworkTransport.Unknown
            }

        val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val wifiInfo = wifiManager?.connectionInfo
        val ssid =
            wifiInfo
                ?.ssid
                ?.removeSurrounding("\"")
                ?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
        val ip =
            wifiInfo
                ?.ipAddress
                ?.takeIf { it != 0 }
                ?.let { Formatter.formatIpAddress(it) }

        return NetworkInfoModel(
            transport = transport,
            ssid = ssid,
            ip = ip,
        )
    }
}
