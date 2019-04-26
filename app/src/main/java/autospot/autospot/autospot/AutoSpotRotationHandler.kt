package autospot.autospot.autospot

import java.lang.Math.atan

class AutoSpotRotationHandler {
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    fun onPosition(pos: Vector2) {
        val RAD_TO_STEPS = 25.78310078
        val spotPos = Vector2(x, y)
        val dir = pos.sub(spotPos)
        val xySteps = atan(dir.y / dir.x) * RAD_TO_STEPS

        val yzSteps = atan(pos.length(spotPos) / y) * RAD_TO_STEPS
    }
}