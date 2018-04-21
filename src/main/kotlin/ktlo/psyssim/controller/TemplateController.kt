package ktlo.psyssim.controller

import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.SolarSystem
import tornadofx.*

class TemplateController: Controller() {

    private fun AstronomicalObject.name(value: String) {
        name = messages[value]
    }

    fun real() = PSSettings().apply {
        star {
            val scaleMass = 1.0
            val scaleFocus = 15.0

            name("sun-star")
            picture(SolarSystem.Sun)
            mass = 12.0

            planet {
                name("mercury-planet")
                picture(SolarSystem.Mercury)
                mass = 0.382*scaleMass
                e = 0.2056
                focus = 0.39*scaleFocus*e
            }

            planet {
                name("venus-planet")
                picture(SolarSystem.Venus)
                mass = 0.949*scaleMass
                e = 0.0068
                focus = 0.72*scaleFocus*e
            }

            planet {
                name("earth-planet")
                picture(SolarSystem.Earth)
                mass = scaleMass
                e = 0.0167
                focus = scaleFocus*e

                planet {
                    name("moon-satellite")
                    picture(SolarSystem.Moon)
                    mass = 0.18
                    e = 0.2482
                    focus = 0.44*scaleFocus*e
                }

            }

            planet {
                name("mars-planet")
                picture(SolarSystem.Mars)
                mass = 0.532*scaleMass
                e = 0.0934
                focus = 1.52*scaleFocus*e
            }

            planet {
                name("jupiter-planet")
                picture(SolarSystem.Jupiter)
                mass = 11.209*scaleMass
                e = 0.0483
                focus = 5.20*scaleFocus*e
            }

            planet {
                name("saturn-planet")
                picture(SolarSystem.Saturn)
                mass = 9.44*scaleMass
                e = 0.056
                focus = 9.54*scaleFocus*e
            }

            planet {
                name("uranus-planet")
                picture(SolarSystem.Uranus)
                mass = 4.007*scaleMass
                e = 0.0461
                focus = 19.18*scaleFocus*e
            }

            planet {
                name("neptune-planet")
                picture(SolarSystem.Neptune)
                mass = 3.883*scaleMass
                e = 0.0097
                focus = 30.06*scaleFocus*e
            }
        }
    }

    fun inner() = PSSettings().apply {
        star {
            val scaleMass = 20.0
            val scaleFocus = 100.0

            name("sun-star")
            picture(SolarSystem.Sun)
            mass = 45.0

            planet {
                name("mercury-planet")
                picture(SolarSystem.Mercury)
                mass = 0.382 * scaleMass
                e = 0.2056
                focus = 0.39 * scaleFocus * e
                angle = 0.6
            }

            planet {
                name("venus-planet")
                picture(SolarSystem.Venus)
                mass = 0.949 * scaleMass
                e = 0.0068
                focus = 0.72 * scaleFocus * e
            }

            planet {
                name("earth-planet")
                picture(SolarSystem.Earth)
                mass = scaleMass
                e = 0.0167
                focus = scaleFocus * e

                planet {
                    name("moon-satellite")
                    picture(SolarSystem.Moon)
                    mass = 0.18*scaleMass
                    e = 0.2482
                    focus = 0.24 * scaleFocus * e
                    angle = 1.0
                }

            }

            planet {
                name("mars-planet")
                picture(SolarSystem.Mars)
                mass = 0.532*scaleMass
                e = 0.0934
                focus = 1.52*scaleFocus*e
            }
        }
    }

    fun outer() = PSSettings().apply {
        star {
            val scaleMass = 1.0
            val scaleFocus = 10.0

            name("sun-star")
            picture(SolarSystem.Sun)
            mass = 12.0

            planet {
                name("jupiter-planet")
                picture(SolarSystem.Jupiter)
                mass = 11.209 * scaleMass
                e = 0.0483
                focus = 5.20 * scaleFocus * e
            }

            planet {
                name("saturn-planet")
                picture(SolarSystem.Saturn)
                mass = 9.44 * scaleMass
                e = 0.056
                focus = 9.54 * scaleFocus * e
            }

            planet {
                name("uranus-planet")
                picture(SolarSystem.Uranus)
                mass = 4.007 * scaleMass
                e = 0.0461
                focus = 19.18 * scaleFocus * e
            }

            planet {
                name("neptune-planet")
                picture(SolarSystem.Neptune)
                mass = 3.883 * scaleMass
                e = 0.0097
                focus = 30.06 * scaleFocus * e
            }
        }
    }

    fun comets() = PSSettings().apply {
        star {
            name("sun-star")
            picture(SolarSystem.Sun)
            mass = 50.0

            planet {
                name("comet")
                picture(SolarSystem.Planet)
                mass = 20.0
                angle = 0.2
                e = .8
                focus = 100.0
            }

            planet {
                name("comet")
                picture(SolarSystem.Pluto)
                mass = 15.0
                angle = 0.5
                e = .85
                focus = 120.0
            }
        }
    }

}