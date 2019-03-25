package autospot.autospot.autospot

import kotlin.math.pow
import kotlin.math.sqrt


data class Position(var x: Double, var y: Double) {

    operator fun plus(position: Position): Position {
        return Position(this.x + position.x, this.y + position.y);
    }

    operator fun minus(position: Position): Position {
        return (position * (-1.0)) + this
    }

    operator fun times(position: Position): Position {
        return Position(this.x * position.x, this.y * position.y)
    }

    operator fun times(i: Double): Position {
        return Position(this.x * i, this.y * i)
    }

    operator fun div(i: Double): Position {
        return this * (1 / i);
    }

    fun normalize(): Position {
        return this / this.lenght()
    }

    fun lenght(): Double {
        return sqrt(this.x.pow(2) + this.y.pow(2))
    }

    companion object {
        fun length(a: Position, b: Position): Double {
            return sqrt((a.x - b.x).pow(2) + (a.y - b.y).pow(2))
        }
    }
}

data class ESPData(var mac: String, var position: Position)

data class MyCircle(var r: Double, var x: Double, var y: Double) {

    fun toPosition(): Position {
        return Position(this.x, this.y)
    }

    companion object {
        fun intersecting2(a: MyCircle, b: MyCircle): List<Position> {
            //based on trigonometry
            //http://csharphelper.com/blog/2014/09/determine-where-two-circles-intersect-in-c/
            val d = sqrt((a.x - b.x).pow(2) + (a.y - b.y).pow(2))
            val a__0 = (a.r.pow(2) - b.r.pow(2) + d.pow(2)) / (2 * d)
            val b__0 = (b.r.pow(2) - a.r.pow(2) + d.pow(2)) / (2 * d)
            val h = sqrt(a.r.pow(2) - a__0.pow(2))
            val c = (a.toPosition() - b.toPosition()).normalize() * a__0
            val diff = ((a.toPosition() - b.toPosition() * h) / d)
            return mutableListOf(c + diff, c - diff)
        }

        fun intersecting(a: MyCircle, b: MyCircle): List<Position> {
            //based on maple isolation
            val X =
                -(a.y.pow(2) - (2 * a.y * b.y) + (a.x - b.x + a.r - b.r) * (a.x - b.x - a.r + b.r)) * (a.y - b.y).pow(2) *
                        (a.y.pow(2) - 2 * b.y * a.y + b.y.pow(2) + (a.x - b.x - a.r - b.r) * (a.x - b.x + a.r + b.r))
            val Xs = mutableListOf(sqrt(X), -sqrt(X)).map {
                (it + a.x.pow(3) - a.x.pow(2) * b.x + (-b.x.pow(2) + a.y.pow(2) - 2 * b.y * a.y + b.y.pow(2)
                        - a.r.pow(2) + b.r.pow(2)) * a.x + b.x.pow(3) + (a.y.pow(2) - 2 * b.y * a.y + b.y.pow(2)
                        + a.r.pow(2) + b.r.pow(2)) * a.x) /
                        (2 * a.x.pow(2) - 4 * a.x * b.x + 2 + b.x.pow(2) + 2 * (a.y - b.y).pow(2))
            }
            val points = Xs.map {
                Position(it, sqrt(a.r.pow(2) - (it - a.x).pow(2)) + a.y)
            }
            return points
        }
    }
}

class AutoSpotNetworkingHandler<T>(val serverThread: T, val esps: List<ESPData>) where T : ServerThread {
    private val rawInputMap = mutableMapOf<String, MutableList<Int>>()

    init {
        if (esps.size != 3) {
            throw IllegalArgumentException("the esps array has to have a size of 3")
        }
        serverThread.addOnMessageListener { rawMessage ->
            rawMessage.split(";").filter {
                !it.isEmpty()
            }.forEach {
                val temp = it.split("=")
                val mac = temp[0]
                val rawInput = temp[1].toIntOrNull()
                if (rawInput != null) {
                    if (rawInputMap.containsKey(mac)) {
                        rawInputMap[mac]!!.add(rawInput)
                        checkForUpdate()
                    } else {
                        if (rawInputMap.keys.size <= 2) {
                            rawInputMap.put(mac, mutableListOf(rawInput))
                        }
                    }
                }
            }

        }
    }

    private fun checkForUpdate() {
        if (
            rawInputMap.keys.map {
                rawInputMap[it]!!
            }.map {
                it.size
            }.all {
                it > 9
            }
        ) {
            rawInputMap.keys.map {
                rawInputMap[it]!!
            }.forEach {
                it.removeAt(0)
                it.removeAt(0)
                it.removeAt(it.size - 1)
                it.removeAt(it.size - 1)
            }
            val data = rawInputMap.toList().map { pair ->
                Pair(esps.find { esp ->
                    esp.mac == pair.first
                }!!.position, pair.second.map { i ->
                    i.toFloat()
                }.average())
            }.map {
                MyCircle(it.second, it.first.x, it.first.y)
            }
            onPosition(calculatePosition(data))
        }
    }

    fun calculatePosition(data: List<MyCircle>): Position {
        val a = arrayOf(
            MyCircle.intersecting(data[0], data[1]),
            MyCircle.intersecting(data[1], data[2]),
            MyCircle.intersecting(data[0], data[2])
        )
        a[0].forEach { a0 ->
            for (i in 1 until a.size) {
                a[i].find {
                    true
                }
            }
        }
        return Position(0.toDouble(), 0.toDouble());
    }

    private val onPositionListenerList = mutableListOf<(position: Position) -> Unit>()
    fun addOnPositionListener(func: (position: Position) -> Unit) {
        onPositionListenerList.add(func)
    }

    fun removeOnPositionListener(func: (position: Position) -> Unit) {
        onPositionListenerList.remove(func)
    }

    protected fun onPosition(position: Position) {
        onPositionListenerList.forEach {
            it(position)
        }
    }
}