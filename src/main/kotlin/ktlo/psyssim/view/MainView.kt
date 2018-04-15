package ktlo.psyssim.view

import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.ImagePattern
import ktlo.psyssim.controller.MainController
import ktlo.psyssim.model.AstronomicalObject
import ktlo.psyssim.model.PSSettings
import ktlo.psyssim.model.PlanetPicture
import ktlo.psyssim.model.PlanetarySystem
import tornadofx.*
import kotlin.math.PI

class MainView: View("Planetary System Simulator") {

    override val root: BorderPane by fxml()

    private val controller: MainController by inject()
    private val settingsPane: PlanetSettingsView by inject()
    val planetarySystem = find(PlanetarySystem::class)/*.apply {
        star = AstronomicalObject().apply {
            val sun = this
            mass = 50.0
            name = "Sun"
            picture = PlanetPicture().apply {
                image = ImagePattern(Image("/ktlo/psyssim/content/sun.png"))
            }
            children += AstronomicalObject().apply {
                val earth = this
                e = 0.64437947941784248641996445035388
                focus = 54.77225575051661134569697828008
                angle = .0
                name = "Earth"
                mass = 20.0
                w = 1.0
                parent = sun
                picture = PlanetPicture().apply { image = ImagePattern(Image("/ktlo/psyssim/content/earth.png")) }

                children += AstronomicalObject().apply {
                    parent = earth
                    e = .2
                    focus = 10.0
                    angle = PI / 4
                    mass = 8.0
                    picture = PlanetPicture().apply { image = ImagePattern(Image("/ktlo/psyssim/content/moon.png")) }
                }
            }
        }
    }
    */

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

    fun pause() {
        planetarySystem.pause(pauseToggle.isSelected)
    }

    fun hideSettings() {

    }

    fun jump() {
        try {
            planetarySystem.jump(jumpField.text.toDouble())
        } catch (e: Exception) {}
    }

    fun save() = controller.save()

    fun saveAs() {
        controller.createFromTemplate(controller.settings)
    }

    fun closePS() {
        planetarySystem.star = PSSettings.empty.star
        replaceWith(StartMenuView::class, ViewTransition.Fade(1.seconds))
    }

}