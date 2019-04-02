package autospot.autospot.autospot


data class ESPData(var mac: String, var position: Vector2)

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
                Circle(Vector2(it.first.x, it.first.y), it.second)
            }
            onPosition(calculatePosition(data))
        }
    }

    fun calculatePosition(data: List<Circle>): Vector2 {
        if (data.size != 3) {
            throw IllegalArgumentException("data can only be 3 long")
        }
        val interSections = mutableListOf<CircleCircleIntersection>()
        for (i in 0 until data.size) {
            for (j in i until data.size) {
                interSections.add(CircleCircleIntersection(data[i], data[j]))
            }
        }
        val vectorList1 = mutableListOf<Vector2>()
        val vectorList2 = mutableListOf<Vector2>()
        vectorList1.add(interSections[0].intersectionPoint1)
        vectorList2.add(interSections[0].intersectionPoint2)
        for (i in 1 until interSections.size) {
            if (interSections[0].intersectionPoint1.length(interSections[i].intersectionPoint1) > interSections[0].intersectionPoint1.length(
                    interSections[i].intersectionPoint2
                )
            ) {
                vectorList1.add(interSections[i].intersectionPoint1)
            } else {
                vectorList1.add(interSections[i].intersectionPoint2)
            }

            if (interSections[0].intersectionPoint2.length(interSections[i].intersectionPoint1) > interSections[0].intersectionPoint2.length(
                    interSections[i].intersectionPoint2
                )
            ) {
                vectorList2.add(interSections[i].intersectionPoint1)
            } else {
                vectorList2.add(interSections[i].intersectionPoint2)
            }
        }
        val vectorList =
            if (vectorList1.map { vectorList1[0].length(it) }.average() > vectorList2.map { vectorList2[0].length(it) }.average()) {
                vectorList1
            } else {
                vectorList2
            }
        return Vector2(vectorList.sumByDouble { it.x } / vectorList.size, vectorList.sumByDouble { it.y } / vectorList.size)
    }

    private val onPositionListenerList = mutableListOf<(position: Vector2) -> Unit>()
    fun addOnPositionListener(func: (position: Vector2) -> Unit) {
        onPositionListenerList.add(func)
    }

    fun removeOnPositionListener(func: (position: Vector2) -> Unit) {
        onPositionListenerList.remove(func)
    }

    protected fun onPosition(position: Vector2) {
        onPositionListenerList.forEach {
            it(position)
        }
    }
}