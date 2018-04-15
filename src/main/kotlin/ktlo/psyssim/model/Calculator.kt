package ktlo.psyssim.model

import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Calculator(model: AstronomicalObject) {
    val e = model.e
    val x: Double
    val y: Double
    val a: Double
    val b: Double
    val c = model.focus
    val alpha: Double
    val beta: Double

    init {
        a = c / model.e
        b = sqrt(a * a - c * c)
        alpha = model.angle
        val sinA = sin(alpha)
        val cosA = cos(alpha)
        val offX = c * cosA
        val offY = c * sinA

        // Holy formulas
        beta = atan(a/b*sinA/cosA)
        val sinB = sin(beta)
        val cosB = cos(beta)
        x = -(a*sinB*cosA - b*sinA*cosB) - offX
        y = -(a*sinB*sinA + b*cosB*cosA) - offY
    }
}
