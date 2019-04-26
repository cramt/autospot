package autospot.autospot.autospot

import java.lang.Math.*

import java.io.Serializable
import kotlin.math.pow

class Vector2(val x: Double, val y: Double) : Serializable {

    fun add(a: Vector2): Vector2 {
        return Vector2(x + a.x, y + a.y)
    }

    fun sub(a: Vector2): Vector2 {
        return Vector2(x - a.x, y - a.y)
    }

    fun neg(): Vector2 {
        return Vector2(-x, -y)
    }

    fun scale(a: Double): Vector2 {
        return Vector2(a * x, a * y)
    }

    fun dot(a: Vector2): Double {
        return x * a.x + y * a.y
    }

    fun modSquared(): Double {
        return dot(this)
    }

    fun mod(): Double {
        return sqrt(modSquared())
    }

    fun normalize(): Vector2 {
        return scale(1 / mod())
    }

    fun rotPlus90(): Vector2 {
        return Vector2(-y, x)
    }

    fun rotMinus90(): Vector2 {
        return Vector2(y, -x)
    }

    fun angle(): Double {
        return atan2(y, x)
    }

    fun length(other: Vector2): Double {
        return sqrt((this.x - other.x).pow(2)+(this.y-other.y).pow(2))
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        var temp: Long
        temp = java.lang.Double.doubleToLongBits(x)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(y)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val other = other as Vector2?
        if (java.lang.Double.doubleToLongBits(x) != java.lang.Double.doubleToLongBits(other!!.x))
            return false
        return java.lang.Double.doubleToLongBits(y) == java.lang.Double.doubleToLongBits(other.y)
    }

    override fun toString(): String {
        return javaClass.simpleName + "(" + x + ", " + y + ")"
    }

    companion object {

        private const val serialVersionUID = 1L

        val NULL = Vector2(0.0, 0.0)
        val X = Vector2(1.0, 0.0)
        val Y = Vector2(0.0, 1.0)

        fun fromAngle(ang: Double): Vector2 {
            return Vector2(cos(ang), sin(ang))
        }

        fun fromPolar(ang: Double, mod: Double): Vector2 {
            return Vector2(mod * cos(ang), mod * sin(ang))
        }
    }

}