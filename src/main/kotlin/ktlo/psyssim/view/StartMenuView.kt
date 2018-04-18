package ktlo.psyssim.view

import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.controller.TemplateController
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.SolarSystem
import tornadofx.*
import kotlin.math.PI

class StartMenuView: View("Planetary System Simulator") {
    private val controller: MainController by inject()
    private val template: TemplateController by inject()
    private val loadPSView: LoadPSView by inject()

    override val root: BorderPane by fxml()

    private val logoView = find(LogoFragment::class)

    private val logoAnimation: Pane by fxid()

    @UIMethod
    fun whenNewPS() {
        controller.createFromTemplate(PSSettings().apply {
            star {
                name = "Star"
                mass = 50.0
                picture(SolarSystem.Sun)
            }
        }, this)
    }

    @UIMethod
    fun whenLoadPS() {
        replaceWith(LoadPSView::class, ViewTransition.Fade(1.seconds))
        loadPSView.updateList()
    }

    @UIMethod
    fun whenTemplatePS() {

        controller.createFromTemplate(template.inner(), this)
    }

    init {
        primaryStage.isResizable = false
        logoAnimation += logoView
    }

}
