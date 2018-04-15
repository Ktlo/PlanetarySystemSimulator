package ktlo.psyssim.view

import javafx.animation.Interpolator
import javafx.animation.PathTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.util.Duration
import ktlo.psyssim.controller.LogoController
import ktlo.psyssim.model.KeplerInterpolator
import tornadofx.*
import java.net.URI

class LogoFragment : Fragment("Logo Animation") {

    private val controller: LogoController by inject()

    override val root = stackpane {

        prefWidth = 350.0
        prefHeight = 200.0

        val halfHeight = prefHeight / 2
        val halfWidth = prefWidth / 2

        style {
            alignment = Pos.CENTER

        }

        circle(halfWidth, halfHeight, 50) {
            fill = ImagePattern(Image("/ktlo/psyssim/content/sun.png"))
            println()
        }

        val pathCircle = controller.createEllipsePath(halfWidth, halfHeight, 100.0, 100.0, .0)
        add(pathCircle)

        val planet = circle(halfWidth - 100, halfHeight, 20) {
            fill = ImagePattern(Image("/ktlo/psyssim/content/earth.png"))
        }

        val transition = PathTransition().apply {
            duration = 5.seconds
            path = pathCircle
            node = planet
            orientation = PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT
            cycleCount = Timeline.INDEFINITE
            isAutoReverse = false
            interpolator = /*KeplerInterpolator()*/ Interpolator.LINEAR
        }.play()

    }

}
