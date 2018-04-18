package ktlo.psyssim.model

import javafx.scene.image.Image

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
    val image: Image by lazy { Image(uri) }

}