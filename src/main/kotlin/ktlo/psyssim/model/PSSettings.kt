package ktlo.psyssim.model

import javafx.beans.property.*
import tornadofx.*
import java.time.LocalDateTime
import javax.json.JsonObject

class PSSettings: JsonModel {

    companion object {
        val empty = PSSettings().apply {
            star {
                mass = 10.0
            }
        }
    }

    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val timestampProperty = SimpleObjectProperty<LocalDateTime>()
    var timestamp by timestampProperty

    val starProperty = SimpleObjectProperty<AstronomicalObject>()
    var star by starProperty

    val lifetimeProperty = SimpleLongProperty()
    var lifetime by lifetimeProperty

    val showPathProperty = SimpleBooleanProperty()
    var showPath by showPathProperty

    override fun updateModel(json: JsonObject) {
        with (json) {
            name = string("name")
            timestamp = datetime("timestamp")
            star = jsonModel("star")
            lifetime = long("lifetime")!!
            showPath = bool("showPath")!!
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with (json) {
            add("name", name)
            add("timestamp", timestamp)
            add("star", star)
            add("lifetime", lifetime)
            add("showPath", showPath)
        }
    }

    inline fun star(lambda: AstronomicalObject.()->Unit) {
        star = AstronomicalObject().apply(lambda)
    }

}