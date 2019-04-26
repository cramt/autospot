package autospot.autospot.autospot

import java.lang.Math.*

// References:
// http://paulbourke.net/geometry/2circle/
// http://mathworld.wolfram.com/Circle-CircleIntersection.html

class CircleCircleIntersection(val c1: Circle, val c2: Circle) {

    // Results valid for all intersections:
    var type: Type
    val distanceC1cC2c: Double

    // Results valid for eccentric circles:
    var radicalPoint: Vector2?
    var distanceC1cRadicalLine: Double
    var distanceC2cRadicalLine: Double
    var versorC1cC2c: Vector2?
    var versorRadicalLine: Vector2?

    // Results valid for tangent circles:
    var intersectionPoint: Vector2?

    // Results valid for overlapping circles:
    var intersectionPoint1: Vector2?
    var intersectionPoint2: Vector2?
    var distanceRadicalPointIntersectionPoints: Double

    // Valid for noncoincident circles:
    val intersectionPoints: Array<Vector2?>
        get() {
            when (type.intersectionPointCount) {
                0 -> return arrayOf()
                1 -> return arrayOf(intersectionPoint)
                2 -> return arrayOf(intersectionPoint1, intersectionPoint2)
                else -> throw IllegalStateException("Coincident circles")
            }
        }

    enum class Type private constructor(// Returns -1 if count is infinite (coincident circles).
        val intersectionPointCount: Int
    ) {

        COINCIDENT(-1),
        CONCENTRIC_CONTAINED(0),
        ECCENTRIC_CONTAINED(0),
        INTERNALLY_TANGENT(1),
        OVERLAPPING(2),
        EXTERNALLY_TANGENT(1),
        SEPARATE(0);

        val isConcentric: Boolean
            get() = this == COINCIDENT || this == CONCENTRIC_CONTAINED

        val isContained: Boolean
            get() = this == CONCENTRIC_CONTAINED || this == ECCENTRIC_CONTAINED

        val isTangent: Boolean
            get() = intersectionPointCount == 1

        val isDisjoint: Boolean
            get() = intersectionPointCount == 0

    }

    init {

        // Vector going from c1 center to c2 center:
        val vectorC1cC2c = c2.c.sub(c1.c)
        // Distance between circle centers:
        distanceC1cC2c = vectorC1cC2c.mod()

        // If circles are concentric there is no radical line, handle the case here:
        if (distanceC1cC2c == 0.0) {
            if (c1.r == c2.r)
                type = Type.COINCIDENT
            else
                type = Type.CONCENTRIC_CONTAINED
            radicalPoint = null
            distanceC1cRadicalLine = 0.0
            distanceC2cRadicalLine = 0.0
            versorC1cC2c = null
            versorRadicalLine = null
            intersectionPoint = null
            intersectionPoint1 = null
            intersectionPoint2 = null
            distanceRadicalPointIntersectionPoints = 0.0

        }
        else {
            // Direction versor from c1 center to c2 center:
            versorC1cC2c = vectorC1cC2c.scale(1 / distanceC1cC2c)
            // Signed distances from circle centers to radical line (the direction to the other center is positive):
            distanceC1cRadicalLine = (sq(distanceC1cC2c) + sq(c1.r) - sq(c2.r)) / (2 * distanceC1cC2c)
            distanceC2cRadicalLine = distanceC1cC2c - distanceC1cRadicalLine
            // Intersection between line connecting circle centers and radical line:
            radicalPoint = c1.c.add(versorC1cC2c!!.scale(distanceC1cRadicalLine))
            // Direction versor of radical line (points to the left if looking from c1 center to c2 center):
            versorRadicalLine = versorC1cC2c!!.rotPlus90()

            // If type had been determined before:
            //switch (type.getIntersectionPointCount()) { ... }

            // Square of distance between radical point and either intersection point, if circles are overlapping:
            val sqH = sq(c1.r) - sq(distanceC1cRadicalLine)
            if (sqH > 0) {
                type = Type.OVERLAPPING
                intersectionPoint = null
                distanceRadicalPointIntersectionPoints = sqrt(sqH)
                intersectionPoint1 =
                    radicalPoint!!.add(versorRadicalLine!!.scale(distanceRadicalPointIntersectionPoints))
                intersectionPoint2 =
                    radicalPoint!!.add(versorRadicalLine!!.scale(-distanceRadicalPointIntersectionPoints))
            } else {
                val external = distanceC1cC2c > max(c1.r, c2.r)
                if (sqH == 0.0) {
                    type = if (external) Type.EXTERNALLY_TANGENT else Type.INTERNALLY_TANGENT
                    intersectionPoint = radicalPoint
                    intersectionPoint1 = null
                    intersectionPoint2 = null
                    distanceRadicalPointIntersectionPoints = 0.0
                } else {
                    type = if (external) Type.SEPARATE else Type.ECCENTRIC_CONTAINED
                    intersectionPoint = null
                    intersectionPoint1 = null
                    intersectionPoint2 = null
                    distanceRadicalPointIntersectionPoints = 0.0
                }
            }
        }
    }

    private fun sq(a: Double): Double {
        return a * a
    }

    override fun toString(): String {
        return (javaClass.simpleName
                + "(c1: " + c1
                + ", c2: " + c2
                + ", type: " + type
                + ", distanceC1cC2c: " + distanceC1cC2c
                + ", radicalPoint: " + radicalPoint
                + ", distanceC1cRadicalLine: " + distanceC1cRadicalLine
                + ", distanceC2cRadicalLine: " + distanceC2cRadicalLine
                + ", versorC1cC2c: " + versorC1cC2c
                + ", versorRadicalLine: " + versorRadicalLine
                + ", intersectionPoint: " + intersectionPoint
                + ", intersectionPoint1: " + intersectionPoint1
                + ", intersectionPoint2: " + intersectionPoint2
                + ", distanceRadicalPointIntersectionPoints: "
                + distanceRadicalPointIntersectionPoints
                + ")")
    }

}