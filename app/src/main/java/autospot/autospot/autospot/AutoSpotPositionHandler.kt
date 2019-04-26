package autospot.autospot.autospot

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlin.math.exp

fun Double.RSSI_TO_MILIMETERS(): Double {
    return exp((-0.08764241893 * this) - 5.233304119) * 1000
}

class AutoSpotPositionHandler(server: ServerThread, public var activity: AppCompatActivity) {
    private val rawInputHashmap = hashMapOf<String, MutableList<Long>>()
    public var interval: Long = 1000
    private var running = true
    public var positions = HashMap<String, Vector2>()


    private val onRawDataListenerList = mutableListOf<(message: HashMap<String, Double>) -> Unit>()
    fun addOnRawDataListener(func: (message: HashMap<String, Double>) -> Unit) {
        onRawDataListenerList.add(func)
    }

    fun removeOnRawDataListener(func: (message: HashMap<String, Double>) -> Unit) {
        onRawDataListenerList.remove(func)
    }

    private fun onRawData(message: HashMap<String, Double>) {
        onRawDataListenerList.forEach {
            Thread {
                it(message)
            }.start()
        }
    }

    private val onPositionListenerList = mutableListOf<(message: Vector2) -> Unit>()
    fun addOnPositionListener(func: (message: Vector2) -> Unit) {
        onPositionListenerList.add(func)
    }

    fun removeOnPositionListener(func: (message: Vector2) -> Unit) {
        onPositionListenerList.remove(func)
    }

    private fun onPosition(message: Vector2) {
        onPositionListenerList.forEach {
            Thread {
                it(message)
            }.start()
        }
    }

    private val onHomeListenerList = mutableListOf<(message: String) -> Unit>()
    fun addOnHomeListener(func: (message: String) -> Unit) {
        onHomeListenerList.add(func)
    }

    fun removeOnHomeListener(func: (message: String) -> Unit) {
        onHomeListenerList.remove(func)
    }

    private fun onHome(message: String) {
        onHomeListenerList.forEach {
            Thread {
                it(message)
            }.start()
        }
    }

    init {
        server.addOnMessageListener {
            val splittet = it.split("=")
            val mac = splittet[0].trim()

            if (mac.contains("home")) {
                onHome(splittet[1])
            } else {
                val value = splittet[1].toLongOrNull()
                if (value != null) {
                    activity.runOnUiThread {
                        if (rawInputHashmap[mac] == null) {
                            rawInputHashmap[mac] = mutableListOf()
                        }
                        rawInputHashmap[mac]!!.add(value)
                    }
                }
            }
        }
        Thread {
            while (running) {
                activity.runOnUiThread {
                    val lhashmap = rawInputHashmap.clone() as HashMap<String, List<Long>>
                    rawInputHashmap.clear()
                    val returnHashMap = hashMapOf<String, Double>()
                    for (key in lhashmap.keys) {
                        returnHashMap[key] = lhashmap[key]!!.average()
                    }
                    onRawData(returnHashMap)
                }
                Thread.sleep(interval)
            }
        }.start()
        addOnRawDataListener { radius ->
            try {
                val circles = radius.keys.map { key ->
                    val pos = positions[key]
                    if (pos != null) {
                        Circle(pos, radius[key]!!.RSSI_TO_MILIMETERS())
                    } else {
                        null
                    }
                }.filter { it != null }.map { it!! }
                if (circles.size == 3) {
                    val pos = Circle.tribleIntersection(circles[0], circles[1], circles[2]);
                    if (pos != null) {
                        onPosition(pos)
                    } else {
                        activity.runOnUiThread {
                            Toast.makeText(activity, "all the circles dont intersect", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Throwable) {
                activity.runOnUiThread {
                    e.printStackTrace()
                    Toast.makeText(activity, "error: " + e.stackTrace, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}