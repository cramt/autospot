package autospot.autospot.autospot

import android.content.*
import android.net.wifi.*

object HotSpotManager {

    //check whether wifi hotspot on or off
    fun IsOn(context: Context): Boolean {
        val wifimanager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            val method = wifimanager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            return method.invoke(wifimanager) as Boolean
        } catch (ignored: Throwable) {
        }

        return false
    }

    // toggle wifi hotspot on or off
    fun Toggle(context: Context): Boolean {
        val wifimanager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wificonfiguration: WifiConfiguration? = null
        try {
            // if WiFi is on, turn it off
            if (IsOn(context)) {
                wifimanager.isWifiEnabled = false
            }
            val method = wifimanager.javaClass.getMethod(
                "setWifiApEnabled",
                WifiConfiguration::class.java,
                Boolean::class.javaPrimitiveType
            )
            method.invoke(wifimanager, wificonfiguration, !IsOn(context))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun Set(context: Context, on: Boolean): Unit {
        if(IsOn(context) != on){
            Toggle(context)
        }
    }
}