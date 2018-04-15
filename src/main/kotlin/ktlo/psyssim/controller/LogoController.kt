package ktlo.psyssim.controller

import javafx.scene.paint.Color
import javafx.scene.shape.*
import tornadofx.*




class LogoController: Controller() {

    fun createEllipsePath(centerX: Double, centerY: Double, radiusX: Double, radiusY: Double, rotate: Double): Path {
        val arcTo = ArcTo().apply {
            x = centerX - radiusX + 1 // to simulate a full 360 degree celcius circle.
            y = centerY - radiusY
            isSweepFlag = false
            isLargeArcFlag = true
            this.radiusX = radiusX
            this.radiusY = radiusY
            xAxisRotation = rotate
        }

        return Path().apply {
            elements.addAll(
                    MoveTo(centerX - radiusX, centerY - radiusY),
                    arcTo,
                    ClosePath()) // close 1 px gap.
            stroke = Color.DODGERBLUE
            strokeDashArray.setAll(5.0, 5.0)
        }
    }


}