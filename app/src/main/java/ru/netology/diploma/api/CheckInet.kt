package ru.netology.diploma.api

import android.R.attr
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.fragment.app.Fragment
import android.R.attr.port
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.*
import android.R.attr.port
import android.util.Log
import ru.netology.diploma.BuildConfig


fun checkServerAvalable(): Boolean {
    return try {
        val url = URL(BuildConfig.BASE_URL)
        val sockaddr: SocketAddress = InetSocketAddress(url.host, url.port)
        val sock = Socket()
        val timeoutMs = 5000
        sock.connect(sockaddr, timeoutMs)
        true
    } catch (e: Exception) {
        false
    }

}

fun checkInternetConnection(context: Context): Boolean {
    val connectivity =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivity == null) {
        return false
    } else if (Build.VERSION.SDK_INT >= 21) {
        val info = connectivity.allNetworks
        if (info != null) {
            for (i in info.indices) {
                if (info[i] != null && connectivity.getNetworkInfo(info[i])!!.isConnected) {
                    return true
                }
            }
        }
    } else {
        val info = connectivity.allNetworkInfo
        if (info != null) {
            for (i in info.indices) {
                if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        val activeNetwork = connectivity.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            return true
        }
    }
    return false
}