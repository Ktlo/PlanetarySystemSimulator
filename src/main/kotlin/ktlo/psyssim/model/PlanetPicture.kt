package ktlo.psyssim.model

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.scene.paint.Paint
import ktlo.psyssim.printErr
import tornadofx.*
import javax.json.JsonObject
import kotlin.math.sqrt

sealed class PlanetPicture: JsonModel {

    class ImagePlanetPicture(): PlanetPicture() {
        private var path: String = ""
        private lateinit var picture: Image

        override val image: Image
            get() = picture

        var uri: String
        get() = path
        set(value) {
            path = value
            picture = Image(value)
            fill = ImagePattern(picture)
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
        var color: String = "#000000"
        set(value) {
            field = value
            fill = Color.web(color)
        }

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
                    color = string("color")!!
                }
                catch (e: Exception) {
                    fill = Color.BLACK
                    printErr(e.message)
                }
            }
        }

        override fun toJSON(json: JsonBuilder) {
            with (json) {
                add("color", color)
            }
        }

    }

    companion object {

        fun fromModel(json: JsonObject): PlanetPicture =
            with (json) {
                when {
                    contains("path") -> toModel<ImagePlanetPicture>()
                    contains("color") -> toModel<ColorPlanetPicture>()
                    else -> ColorPlanetPicture()
                }
            }

    }

    val fillProperty = SimpleObjectProperty<Paint>(Color.BLACK)
    var fill: Paint by fillProperty

    abstract val image: Image

}