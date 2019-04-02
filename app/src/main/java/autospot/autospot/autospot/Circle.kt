package autospot.autospot.autospot

import java.io.Serializable

class Circle(val c: Vector2?, val r: Double) : Serializable {

    init {
        if (r <= 0) throw IllegalArgumentException("Radius must be positive")
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (c?.hashCode() ?: 0)
        val temp: Long
        temp = java.lang.Double.doubleToLongBits(r)
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
        if (c == null) {
            if (other!!.c != null)
                return false
        } else if (c != other!!.c)
            return false
        return if (java.lang.Double.doubleToLongBits(r) != java.lang.Double.doubleToLongBits(other.r)) false else true
    }

    override fun toString(): String {
        return javaClass.simpleName + "(c: " + c + ", r: " + r + ")"
    }

    companion object {

        private const val serialVersionUID = 1L
    }

}