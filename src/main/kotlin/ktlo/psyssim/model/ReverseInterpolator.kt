package ktlo.psyssim.model

import javafx.animation.Interpolator

object ReverseInterpolator: Interpolator() {
    override fun curve(t: Double) = 1.0 - t
}