package ktlo.psyssim.model

import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.scene.paint.Paint
import ktlo.psyssim.printErr
import tornadofx.*
import javax.json.JsonObject
import tornadofx.getValue
import tornadofx.setValue

class PlanetPicture: JsonModel {

    companion object {
        val empty = PlanetPicture()
    }

    val pathProperty = SimpleStringProperty(null)
    var path by pathProperty

    val colorProperty = SimpleStringProperty(null)
    var color by colorProperty

    val imageProperty = SimpleObjectProperty<Paint>(Color.BLACK)
    var image by imageProperty

    init {
        pathProperty.addListener { _ ->
            try {
                val p = path
                if (p != null)
                    image = ImagePattern(Image(p))
            }
            catch (e: Exception) {}
        }
        colorProperty.addListener { _ ->
            try {
                val c = color
                if (c !== null) {
                    image = Color.web(c)
                }
            }
            catch (e: Exception) {}
        }
    }

    override fun updateModel(json: JsonObject) {
        with (json) {
            try {
                if (contains("path")) {
                    path = string("path")
                    image = ImagePattern(Image(path))
                }
            }
            catch (e: Exception) {
                path = null
                image = Color.BLACK
                printErr(e.message)
            }
            try {
                if (contains("color")) {
                    color = string("color")
                    image = Color.web(color)
                }
            }
            catch (e: Exception) {
                printErr(e.message)
            }
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with (json) {
            when {
                path !== null -> add("path", path)
                color !== null -> add("color", color)
                else -> {
                }
            }
        }
    }

}