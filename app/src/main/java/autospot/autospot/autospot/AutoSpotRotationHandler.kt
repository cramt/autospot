package autospot.autospot.autospot

import android.util.Log
import java.lang.Math.atan
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class AutoSpotRotationHandler {
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var ip: String = ""
    fun onPosition(pos: Vector2) {
        val RAD_TO_STEPS = 25.78310078
        val spotPos = Vector2(x, y)
        val dir = pos.sub(spotPos)
        val xySteps = atan(dir.y / dir.x) * RAD_TO_STEPS

        val yzSteps = atan(pos.length(spotPos) / y) * RAD_TO_STEPS
    }

    init {
        Thread {
            while(true){
                Log.v("ip", ip)
                if(ip != ""){
                    try {
                        val input = "ding dong"
                        val bytes = input.toByteArray()
                        val socket = DatagramSocket()
                        val packet = DatagramPacket(bytes, bytes.size, InetAddress.getByName(ip), 69)
                        socket.broadcast = true
                        socket.send(packet)
                        socket.close()
                    }
                    catch (err: Error){
                        Log.v("err", err.message)
                    }
                }
                Thread.sleep(1000)
            }
        }.start()
    }
}