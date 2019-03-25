package autospot.autospot.autospot

import java.net.DatagramPacket
import java.net.DatagramSocket

abstract class ServerThread : Thread() {
    private val onMessageListenerList = mutableListOf<(message: String) -> Unit>()
    fun addOnMessageListener(func: (message: String) -> Unit) {
        onMessageListenerList.add(func)
    }

    fun removeOnMessageListener(func: (message: String) -> Unit) {
        onMessageListenerList.remove(func)
    }

    protected fun onMessage(message: String) {
        onMessageListenerList.forEach {
            Thread {
                it(message)
            }.start()
        }
    }

    private val onErrorListenerList = mutableListOf<(message: Throwable) -> Unit>()
    fun addOnErrorListener(func: (message: Throwable) -> Unit) {
        onErrorListenerList.add(func)
    }

    fun removeOnErrorListener(func: (message: Throwable) -> Unit) {
        onErrorListenerList.add(func)
    }

    protected fun onError(message: Throwable) {
        onErrorListenerList.forEach {
            Thread {
                it(message)
            }.start()
        }
    }
}

class UDPServerThread : ServerThread() {
    private var bKeepRunning = true

    override fun run() {
        var message: String
        val lmessage = ByteArray(16384)
        val packet = DatagramPacket(lmessage, lmessage.size)
        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket(6969)

            while (bKeepRunning) {
                socket!!.receive(packet)
                message = String(lmessage, 0, packet.length)

                onMessage(message)
            }
        } catch (e: Throwable) {
            onError(e)
            e.printStackTrace()
        }


        socket?.close()
    }
}