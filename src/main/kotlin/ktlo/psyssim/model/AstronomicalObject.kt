package ktlo.psyssim.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.Group
import javafx.scene.shape.Path
import tornadofx.*
import javax.json.JsonObject
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import tornadofx.getValue
import tornadofx.setValue

class AstronomicalObject: JsonModel {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val massProperty = SimpleDoubleProperty()
    var mass by massProperty

    val aProperty = SimpleDoubleProperty()
    var a by aProperty

    val bProperty = SimpleDoubleProperty()
    var b by bProperty

    val angleProperty = SimpleDoubleProperty()
    var angle by angleProperty

    val wProperty = SimpleDoubleProperty()
    var w by wProperty

    val positionProperty = SuperSimpleDoubleProperty()
    var position by positionProperty

    val pictureProperty = SimpleObjectProperty<PlanetPicture>()
    var picture: PlanetPicture by pictureProperty

    val children = FXCollections.observableArrayList<AstronomicalObject>()!!

    var parent: AstronomicalObject? = null

    lateinit var associatedGroup: Group
    var associatedOrbit: Path? = null

    var beta = .0
    var focus = .0
    var e = .0
    var x = .0
    var y = .0

    fun recalculateValues() {
        focus = sqrt(a*a - b*b)
        e = focus / a
        val sinA = sin(angle)
        val cosA = cos(angle)
        val offX = focus * cosA
        val offY = focus * sinA

        // Holy formulas
        beta = atan(a/b*sinA/cosA)
        val sinB = sin(beta)
        val cosB = cos(beta)
        x = -(a*sinB*cosA - b*sinA*cosB) - offX
        y = -(a*sinB*sinA + b*cosB*cosA) - offY
    }

    override fun updateModel(json: JsonObject) {
        with (json) {
            name = string("name") ?: ""
            mass = double("mass") ?: 19.5
            if ("focus" in json && "e" in json) {
                val c = double("focus")!!
                val e = double("e")!!
                a = c / e
                b = sqrt(a*a - c*c)
            }
            else {
                a = double("a") ?: 50.0
                b = double("b") ?: 50.0
            }
            angle = double("angle") ?: .0
            w = double("w") ?: .0
            position = double("way") ?: .0
            picture = if ("picture" in json)
                PlanetPicture.fromModel(getJsonObject("picture"))
            else
                PlanetPicture.ColorPlanetPicture()
            if ("children" in json)
                children.setAll(getJsonArray("children").toModel())
        }
        children.forEach { it.parent = this }
    }

    override fun toJSON(json: JsonBuilder) {
        with (json) {
            add("name", name)
            add("mass", mass)
            add("a", a)
            add("b", b)
            add("angle", angle)
            add("w", w)
            add("way", position)
            add("picture", picture)
            add("children", children.toJSON())
        }
    }

    inline fun planet(lambda: AstronomicalObject.()->Unit): AstronomicalObject {
        val planet = AstronomicalObject().apply(lambda)
        val c = planet.focus
        val a = c/planet.e
        planet.a = a
        planet.b = sqrt(a*a - c*c)
        planet.parent = this
        children += planet
        return planet
    }

    fun picture(resource: SolarSystem) {
        deleteOldImage()
        picture = PlanetPicture.ImagePlanetPicture(resource)
    }

    fun color(value: String) {
        deleteOldImage()
        picture = PlanetPicture.ColorPlanetPicture().apply { colorString = value }
    }

    fun picture(imageFile: String) {
        deleteOldImage()
        picture = PlanetPicture.ImagePlanetPicture().apply { uri = imageFile }
    }

    private fun deleteOldImage() {
        val pic = picture
        if (pic is PlanetPicture.ImagePlanetPicture) {
            pic.uri = ""
        }
    }

}
