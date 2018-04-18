package ktlo.psyssim.view

import javafx.animation.Interpolator
import javafx.animation.PathTransition
import javafx.animation.Timeline
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import ktlo.psyssim.model.SolarSystem
import tornadofx.*

class LogoFragment : Fragment() {

    override val root = stackpane {

        prefWidth = 350.0
        prefHeight = 200.0

        val halfHeight = prefHeight / 2
        val halfWidth = prefWidth / 2

        alignment = Pos.CENTER

        circle(halfWidth, halfHeight, 50) {
            fill = ImagePattern(SolarSystem.Sun.image)
            println()
        }

        val orbit = circlePath(halfWidth, halfHeight, 100.0)

        val planet = circle(halfWidth - 100, halfHeight, 20) {
            fill = ImagePattern(SolarSystem.Earth.image)
        }

        PathTransition().apply {
            duration = 5.seconds
            path = orbit
            node = planet
            orientation = PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT
            cycleCount = Timeline.INDEFINITE
            isAutoReverse = false
            interpolator = Interpolator.LINEAR
        }.play()

    }

    private fun Pane.circlePath(centerX: Double, centerY: Double, radius: Double) = path {
        moveTo(centerX - radius, centerY - radius)
        arcTo {
            x = centerX - radius + 1
            y = centerY - radius
            isSweepFlag = false
            isLargeArcFlag = true
            radiusX = radius
            radiusY = radius
        }
        closepath()

        stroke = Color.DARKBLUE
        strokeDashArray.setAll(5.0, 5.0)
    }

}
