package ktlo.psyssim.view

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.SolarSystem
import tornadofx.*
import java.net.URI

class PlanetLookView: Fragment() {
    private val controller: MainController by inject()
    var planet = AstronomicalObject()
    set(value) {
        colorPicker.value = value.picture.color
        picture.image = value.picture.image
        field = value
    }
    var picture: ImageView by singleAssign()
    var planetIcon: ImageView by singleAssign()
    var colorPicker: ColorPicker by singleAssign()
    var variants: ComboBox<SolarSystem> by singleAssign()

    init {
        title = controller.programName
    }

    override val root = vbox(6, Pos.TOP_CENTER) {
        prefHeight = 400.0
        prefWidth = 120.0

        style {
            backgroundImage += URI(resources["/ktlo/psyssim/content/stars.jpg"])
            backgroundInsets += box(100.percent)
        }

        picture = imageview()

        tabpane {
            prefHeight = 200.0
            style {
                backgroundColor += Color.WHITE
            }
            tab(messages["toggle.image"]) {
                isClosable = false
                vbox(alignment = Pos.CENTER) {
                    button(messages["image.chooseFile"]) {
                        action {
                            controller.imageLoader(planet)
                            picture.image = planet.picture.image
                        }
                    }
                }
            }
            tab(messages["toggle.color"]) {
                isClosable = false
                vbox(alignment = Pos.TOP_CENTER) {
                    colorPicker = colorpicker {
                        onAction = EventHandler {
                            val colorString = value.toString()
                            planet.color(colorString)
                            picture.image = planet.picture.image
                        }
                    }
                }
            }
            tab(messages["toggle.library"]) {
                isClosable = false
                vbox(10, Pos.TOP_CENTER) {
                    hbox(5, Pos.CENTER) {
                        variants = combobox(values = SolarSystem.values().toList()) {
                            value = SolarSystem.Sun
                            onAction = EventHandler {
                                planetIcon.image = selectedItem!!.image
                            }
                        }
                        planetIcon = imageview(variants.selectedItem!!.image) {
                            fitWidth = 50.0
                            fitHeight = 50.0
                        }
                    }
                    button(messages["library.select"]) {
                        action {
                            val value = variants.selectedItem!!
                            planet.picture(value)
                            picture.image = value.image
                        }
                    }
                }
            }
        }
    }

}