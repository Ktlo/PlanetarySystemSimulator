package ktlo.psyssim.model

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.scene.paint.Paint
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.printErr
import tornadofx.*
import java.io.File
import javax.json.JsonObject
import kotlin.math.sqrt

sealed class PlanetPicture: JsonModel {

    class ImagePlanetPicture(): PlanetPicture() {
        private val controller = find(MainController::class)
        private var path: String = ""
        private lateinit var picture: Image

        override val image: Image
            get() = picture

        override val color = Color.BLACK!!

        var uri: String
        get() = path
        set(value) {
            try {
                val protocol = controller.protocol
                if (path.startsWith(protocol, true)) {
                    File(controller.contentDirectory, path.substring(protocol.length)).delete()
                }
                path = value
                if (value.isBlank())
                    return
                picture = if (value.startsWith(protocol, true)) {
                    val file = File(controller.contentDirectory, value.substring(protocol.length)).absolutePath
                    Image("file:$file", 256.0, 256.0, false, false)
                } else
                    Image(value, 256.0, 256.0, false, false)
                fill = ImagePattern(picture)
            } catch (e: Exception) {
                picture = SolarSystem.Planet.image
                path = SolarSystem.Planet.uri
                fill = ImagePattern(picture)
            }
        }

        constructor(planet: SolarSystem) : this() {
            path = planet.uri
            picture = planet.image
            fill = ImagePattern(planet.image)
        }

        override fun updateModel(json: JsonObject) {
            with (json) {
                try {
                    uri = string("path")!!
                }
                catch (e: Exception) {
                    path = ""
                    fill = Color.BLACK
                    printErr(e.message)
                }
            }
        }

        override fun toJSON(json: JsonBuilder) {
            with (json) {
                add("path", path)
            }
        }

    }

    class ColorPlanetPicture: PlanetPicture() {
        var colorString: String = "#000000"
            set(value) {
                field = value
                fill = color
            }

        override val color
            get() = Color.web(colorString) ?: Color.BLACK!!

        override val image: Image
            get() {
                val d = 250.0
                val center = d/2.0
                val scaled = (sqrt(2.0) - 1.0) * center

                val gc = Canvas(d, d).graphicsContext2D
                gc.fill = fill
                gc.fillOval(scaled, scaled, center, center)
                val result = WritableImage(d.toInt(), d.toInt())
                gc.drawImage(result, d, d)
                return result
            }

        override fun updateModel(json: JsonObject) {
            with (json) {
                try {
                    colorString = string("colorString")!!
                }
                catch (e: Exception) {
                    fill = Color.BLACK
                    printErr(e.message)
                }
            }
        }

        override fun toJSON(json: JsonBuilder) {
            with (json) {
                add("colorString", colorString)
            }
        }

    }

    companion object {

        fun fromModel(json: JsonObject): PlanetPicture =
            with (json) {
                when {
                    contains("path") -> toModel<ImagePlanetPicture>()
                    contains("colorString") -> toModel<ColorPlanetPicture>()
                    else -> ColorPlanetPicture()
                }
            }

    }

    val fillProperty = SimpleObjectProperty<Paint>(Color.BLACK)
    var fill: Paint by fillProperty

    abstract val image: Image
    abstract val color: Color

}