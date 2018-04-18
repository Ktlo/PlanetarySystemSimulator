package ktlo.psyssim.view

import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.layout.BorderPane
import ktlo.psyssim.UIMethod
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.PlanetarySystem
import tornadofx.*

class MainView: View("Planetary System Simulator") {

    override val root: BorderPane by fxml()

    private val controller: MainController by inject()
    private val settingsPane: PlanetSettingsView by inject()
    val planetarySystem = find(PlanetarySystem::class)

    val graphicalLook: BorderPane by fxid()
    private val frequencySlider: Slider by fxid()
    private val jumpField: TextField by fxid()
    private val pauseToggle: ToggleButton by fxid()

    init {
        frequencySlider.valueProperty().addListener { _, _, newValue ->
            planetarySystem.frequency = newValue.toDouble()
        }

        planetarySystem.onSelection {
            with(graphicalLook) {
                if (left == null) {
                    left = settingsPane.root
                }
                settingsPane.selectPlanet(it)
            }
        }

        with (graphicalLook) {
            center += planetarySystem
        }
    }

    @UIMethod
    fun pause() {
        planetarySystem.pause(pauseToggle.isSelected)
    }

    @UIMethod
    fun hideSettings() {

    }

    @UIMethod
    fun jump() {
        try {
            planetarySystem.jump(jumpField.text.toDouble())
        } catch (e: Exception) {}
    }

    @UIMethod
    fun save() = controller.save()

    @UIMethod
    fun saveAs() {
        controller.createFromTemplate(controller.settings)
        save()
    }

    @UIMethod
    fun closePS() {
        planetarySystem.star = PSSettings.empty.star
        primaryStage.isResizable = false
        primaryStage.width = 600.0
        primaryStage.height = 400.0
        replaceWith(StartMenuView::class, ViewTransition.Fade(1.seconds))
    }

}