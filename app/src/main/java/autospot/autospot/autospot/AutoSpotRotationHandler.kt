package autospot.autospot.autospot

import android.util.Log
import java.lang.Math.atan
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.math.roundToInt

class AutoSpotRotationHandler {
    var on: Boolean = false
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var ip: String = ""
    fun onPosition(pos: Vector2) {
        val RAD_TO_DEG = 57.2957795;
        val spotPos = Vector2(x, y)
        val dir = pos.sub(spotPos)
        val xySteps = (atan(dir.y / dir.x) * RAD_TO_DEG * 4).roundToInt()

        val yzSteps = (90 - (atan(pos.length(spotPos) / y) * RAD_TO_DEG * 2.666666)).roundToInt() * (-1)
        sendUDPPacket(xySteps.toString() + "," + yzSteps.toString())
    }

    fun sendUDPPacket(str: String): Boolean {
        if(!on){
            return false
        }
        var address: InetAddress? = null
        try {
            address = InetAddress.getByName(ip)
        } catch (e: Error) {

        }
        if (ip != "" && address != null) {
            try {
                val bytes = str.toByteArray()
                val socket = DatagramSocket()
                val packet = DatagramPacket(bytes, bytes.size, address, 69)
                socket.broadcast = true
                socket.send(packet)
                socket.close()
                return true
            } catch (err: Error) {
                Log.v("err", err.message)
            }
        }
        return false
    }

    private val onStepsListenerList = mutableListOf<(xy: Int, yz: Int) -> Unit>()
    fun addOnStepsListener(func: (xy: Int, yz: Int) -> Unit) {
        onStepsListenerList.add(func)
    }

    fun removeOnStepsListener(func: (xy: Int, yz: Int) -> Unit) {
        onStepsListenerList.remove(func)
    }

    private fun onSteps(xy: Int, yz: Int) {
        onStepsListenerList.forEach {
            Thread {
                it(xy, yz)
            }.start()
        }
    }

    init {

    }
}