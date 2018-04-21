package ktlo.psyssim.model

import javafx.scene.image.Image
import ktlo.psyssim.controller.TemplateController
import tornadofx.*

enum class SolarSystem(name: String) {
    Earth("earth-planet"),
    Jupiter("jupiter-planet"),
    Mars("mars-planet"),
    Mercury("mercury-planet"),
    Moon("moon-satellite"),
    Neptune("neptune-planet"),
    Pluto("pluto-dwarf-planet"),
    Saturn("saturn-planet"),
    Sun("sun-star"),
    Uranus("uranus-planet"),
    Venus("venus-planet"),

    Planet("planet");

    val uri = "/ktlo/psyssim/content/$name.png"
    private val string = find(TemplateController::class).messages[name]!!
    val image: Image by lazy { Image(uri, 256.0, 256.0, false, false) }

    override fun toString() = string

}