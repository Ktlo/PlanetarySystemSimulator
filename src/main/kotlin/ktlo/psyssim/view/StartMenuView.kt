package ktlo.psyssim.view

import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.PSSettings
import tornadofx.*
import kotlin.math.PI

class StartMenuView: View("Planetary System Simulator") {
    private val controller: MainController by inject()
    private val loadPSView: LoadPSView by inject()

    override val root: BorderPane by fxml()

    val newPSButton: Button by fxid()
    val loadPSButton: Button by fxid()
    val templatePSButton: Button by fxid()

    private val logoView = find(LogoFragment::class)

    private val logoAnimation: Pane by fxid()

    fun whenNewPS() {
        controller.createFromTemplate(PSSettings().apply {
            star {
                name = "Star"
                mass = 50.0
                picture("/ktlo/psyssim/content/sun.png")
            }
        }, this)
    }

    fun whenLoadPS() {
        replaceWith(LoadPSView::class, ViewTransition.Fade(1.seconds))
        loadPSView.updateList()
    }
    fun whenTemplatePS() {
        controller.createFromTemplate(PSSettings().apply {
            star {
                name = messages["template.sun"]
                mass = 50.0
                picture("/ktlo/psyssim/content/sun.png")

                planet {
                    name = messages["template.earth"]
                    mass = 20.0
                    e = 0.64437947941784248641996445035388
                    focus = 54.77225575051661134569697828008
                    angle = .5
                    w = 1.0
                    picture("/ktlo/psyssim/content/earth.png")

                    planet {
                        name = messages["template.moon"]
                        e = .2
                        focus = 10.0
                        angle = PI / 4
                        mass = 8.0
                        picture("/ktlo/psyssim/content/moon.png")
                    }
                }

                planet {
                    name = messages["template.planet"]
                    e = .2
                    focus = 50.0
                    w = 3.5
                    mass = 15.0
                    picture("/ktlo/psyssim/content/planet.png")
                }
            }
        }, this)
    }

    init {
        logoAnimation += logoView
    }

}
