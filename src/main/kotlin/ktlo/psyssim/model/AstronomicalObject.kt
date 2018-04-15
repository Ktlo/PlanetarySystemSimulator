package ktlo.psyssim.model

import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.scene.Group
import javafx.scene.shape.Path
import tornadofx.*
import javax.json.JsonObject
import tornadofx.getValue
import tornadofx.setValue
import kotlin.reflect.KProperty

class AstronomicalObject: JsonModel {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val massProperty = SimpleDoubleProperty()
    var mass by massProperty

    val focusProperty = SimpleDoubleProperty()
    var focus by focusProperty

    val eProperty = SimpleDoubleProperty()
    var e by eProperty

    val angleProperty = SimpleDoubleProperty()
    var angle by angleProperty

    val wProperty = SimpleDoubleProperty()
    var w by wProperty

    val positionProperty = SuperSimpleDoubleProperty()
    var position by positionProperty

    val pictureProperty = SimpleObjectProperty<PlanetPicture>(PlanetPicture.empty)
    var picture by pictureProperty

    val children = FXCollections.observableArrayList<AstronomicalObject>()

    var parent: AstronomicalObject? = null

    lateinit var associatedGroup: Group
    var associatedOrbit: Path? = null

    override fun updateModel(json: JsonObject) {
        with (json) {
            name = string("name")
            mass = double("mass")!!
            focus = double("focus")!!
            e = double("e")!!
            angle = double("angle")!!
            w = double("w")!!
            position = double("way") ?: .0
            picture = if (contains("picture"))
                getJsonObject("picture").toModel()
            else
                PlanetPicture()
            if (contains("children"))
                children.setAll(getJsonArray("children").toModel())
            children.forEach { it.parent = this@AstronomicalObject }
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with (json) {
            add("name", name)
            add("mass", mass)
            add("focus", focus)
            add("e", e)
            add("angle", angle)
            add("w", w)
            add("way", position)
            add("picture", picture)
            add("children", children.toJSON())
        }
    }

    inline fun planet(lambda: AstronomicalObject.()->Unit): AstronomicalObject {
        val planet = AstronomicalObject().apply(lambda)
        planet.parent = this
        children += planet
        return planet
    }

    fun picture(imageFile: String) {
        picture = PlanetPicture().apply { path = imageFile }
    }

}
