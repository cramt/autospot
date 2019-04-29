package autospot.autospot.autospot

import android.widget.Toast
import java.io.Serializable

class Circle(val c: Vector2, val r: Double) : Serializable {
    init {
        if (r <= 0) throw IllegalArgumentException("Radius must be positive")
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + c.hashCode()
        val temp = java.lang.Double.doubleToLongBits(r)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj)
            return true
        if (obj == null)
            return false
        if (javaClass != obj.javaClass)
            return false
        val other = obj as Circle?
        if (c != other!!.c)
            return false
        return java.lang.Double.doubleToLongBits(r) == java.lang.Double.doubleToLongBits(other.r)
    }

    override fun toString(): String {
        return javaClass.simpleName + "(c: " + c + ", r: " + r + ")"
    }

    companion object {
        fun tripleIntersection(c1: Circle, c2: Circle, c3: Circle): Vector2? {
            val intersections = arrayOf(
                CircleCircleIntersection(c1, c2),
                CircleCircleIntersection(c2, c3),
                CircleCircleIntersection(c3, c1)
            )
            if (!intersections.all {
                    it.intersectionPoints.size == 2
                }) {
                return null
            }
            val vectorList1 = mutableListOf<Vector2>()
            val vectorList2 = mutableListOf<Vector2>()
            vectorList1.add(intersections[0].intersectionPoint1!!)
            vectorList2.add(intersections[0].intersectionPoint2!!)
            for (i in 1 until intersections.size) {
                if (intersections[0].intersectionPoint1!!.length(intersections[i].intersectionPoint1!!) < intersections[0].intersectionPoint1!!.length(
                        intersections[i].intersectionPoint2!!
                    )
                ) {
                    vectorList1.add(intersections[i].intersectionPoint1!!)
                } else {
                    vectorList1.add(intersections[i].intersectionPoint2!!)
                }

                if (intersections[0].intersectionPoint2!!.length(intersections[i].intersectionPoint1!!) < intersections[0].intersectionPoint2!!.length(
                        intersections[i].intersectionPoint2!!
                    )
                ) {
                    vectorList2.add(intersections[i].intersectionPoint1!!)
                } else {
                    vectorList2.add(intersections[i].intersectionPoint2!!)
                }
            }
            val vectorList =
                if (vectorList1.map { vectorList1[0].length(it) }.average() < vectorList2.map { vectorList2[0].length(it) }.average()) {
                    vectorList1
                } else {
                    vectorList2
                }
            return Vector2(vectorList.sumByDouble { it.x } / vectorList.size,
                vectorList.sumByDouble { it.y } / vectorList.size)
        }

        private const val serialVersionUID = 1L
    }

}