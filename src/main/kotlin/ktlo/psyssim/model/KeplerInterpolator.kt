package ktlo.psyssim.model

import javafx.animation.Interpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

class KeplerInterpolator(calculator: Calculator, private val position: SuperSimpleDoubleProperty): Interpolator() {

    private var lastT = position.value
    private var lastR = integrate(.0, lastT, this::f)
    private val e = calculator.e
    private val k1 = 2*e
    private val k2 = e*e
    private var max = integrate(.0, 1.0, this::f)

    private val startValue: Double

    init {
        val start = integrate(.0, calculator.beta, this::length)
        val end = integrate(.0, 2*PI, this::length)
        startValue = 0.75 - start / end
    }

    private fun f(t: Double) = 2 * (sqrt(1 + k1*cos(2*PI*t) + k2))
    private fun length(x: Double): Double {
        val cosValue = cos(x)
        return sqrt(1 - k2*cosValue*cosValue)
    }

    private fun integrate(t0: Double, t1: Double, lambda: (x: Double) -> Double): Double {
        val dt = 0.0001
        var t = t0
        var r = .0
        var sum = .0

        do {
            val newR = lambda(t)
            sum += (newR + r)
            r = newR
            t += dt
        } while (t < t1)

        return sum / 2 * dt
    }

    private val way: Double
    get() {
        val r = lastR / max + startValue
        return if (r > 1.0) r - 1.0 else r
    }

    override fun curve(t: Double): Double {
        val dt = t - lastT
        if (dt < 0) {
            max = lastR
            lastR = .0
            lastT = .0
            return startValue + f(t) * dt
        }
        val newR = f(t)
        lastR += newR*dt
        //val newR = integrate(lastT, t, this::f)
        //lastR += newR
        lastT = t
        position.value = t
        return way
    }

}