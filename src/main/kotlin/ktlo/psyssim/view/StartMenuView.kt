package ktlo.psyssim.view

import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.controller.TemplateController
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.SolarSystem
import tornadofx.*
import kotlin.math.PI

class StartMenuView: View() {
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
        val templateView = find(LoadTemplateView::class)
        templateView.openModal(StageStyle.UTILITY, resizable = false, block = true)
        if (templateView.result) {
            controller.createFromTemplate(templateView.selected.settings, this)
        }
    }

    init {
        title = controller.programName
        primaryStage.icons.add(SolarSystem.Sun.image)
        primaryStage.isResizable = false
        logoAnimation += logoView
    }

}
